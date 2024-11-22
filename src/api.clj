(ns gravie.underwriting.quote.api
  (:require
    [clojure.java.io :as io]
    [clojure.string :as s]
    [gravie.underwriting.cli.idp.logger :as log]
    [ring.util.response :as ring]
    [gravie.underwriting.utils :refer [base-64-decode wrap-log-execution-time big-decimalize safe-parse-number get-contract-type]]
    [gravie.underwriting.quote.validation :refer [validate-input! validate-update-input-details!]]
    [gravie.underwriting.quote.management :as manager]
    [gravie.underwriting.quote.manual-input-file.initialization :as manual-input-file-init]
    [gravie.underwriting.quote.manual-input-file.management :as manual-input-file-manager]
    [gravie.underwriting.quote-event.management :as event-manager]
    [gravie.underwriting.quote.input-data :as input-data-manager]
    [gravie.underwriting.quote.idp.manager :as idp-manager]
    [gravie.underwriting.quote.assembly :as quote-assembly-manager]
    [gravie.underwriting.api.authentication :refer [auth-token-interceptor]]
    [gravie.underwriting.api.quote-lock-down :refer [quote-lock-down-interceptor]]
    [toucan.db :as db]
    [taoensso.timbre :as l]))

(defn warn-input-conflict [message quote-id]
  (l/warn "input-data/calculation-conflict" {:quote-id quote-id})
  (-> {:success false
       :message message}
      ring/response
      (ring/status 409)))

(defn create-quote [{:keys [name] :as params}]
  (if (not name)
    (-> {:success false
         :message "Could not create quote. Missing quote name."}
        ring/response
        (ring/status 400))
    (-> {:success true
         :quote (manager/create-quote params)}
        ring/response
        (ring/status 201))))

(defn copy-quote [{:keys [quote-id preserve]}]
  (let [copy-inputs? (some #{"inputs"} preserve)
        copy-adjustments? (some #{"adjustments"} preserve)]
    (if (not quote-id)
      (-> {:success false
           :message "Could not copy quote. Missing quote ID."}
          ring/response
          (ring/status 400))
      (if (and copy-adjustments? (not copy-inputs?))
        (-> {:success false
             :message "Could not copy quote due to conflicting parameters. Cannot copy adjustments without copying input data."}
            ring/response
            (ring/status 409))
        (-> {:success true
             :quote (manager/copy-quote quote-id {:copy-input-data copy-inputs?
                                                  :copy-quote-adjustments copy-adjustments?})}
            ring/response
            (ring/status 201))))))

;; todo: should all of these endpoints return the same property "result" in the response?
(defn post-quote
  [{:keys [json-params]}]
  (if-let [copy-params (:copy json-params)]
    (copy-quote copy-params)
    (create-quote json-params)))

(defn put-quote
  [{:keys [path-params json-params]}]
  (ring/response {:success true
                  :quote (manager/update-quote (:id path-params) json-params)}))

(defn get-quotes
  [{:keys [query-params]}]
  ;; TODO - query-params aren't converted to kebab-case here for some reason
  (let [external-id (:externalId query-params)
        query-string (:queryString query-params)
        limit (some-> query-params :limit parse-long)
        offset (some-> query-params :offset parse-long)
        sort-by (some-> query-params :sortBy read-string)]
    (cond
      (and offset (nil? limit))
      (-> (ring/bad-request {:success       false
                             :error-details "If offset is specified, then limit is required."})
          (ring/status 422))
      (and (not-empty sort-by) (not (contains? sort-by :field)))
      (-> (ring/bad-request {:success       false
                             :error-details "Provided sort by is incorrect, it should have field and order keys"})
          (ring/status 400))
      :else (-> (manager/get-quotes {:external-id  external-id
                                     :query-string query-string
                                     :limit        limit
                                     :offset       offset
                                     :sort-by      sort-by})
                (merge {:success true})
                (ring/response)))))

(defn get-quote
  [{:keys [path-params query-params]}]
  (let [get-plan-ids (fn [plan-ids]
                       (when (seq plan-ids)
                         (->> (s/split plan-ids #",")
                              (keep safe-parse-number))))
        plan-ids (get-plan-ids (:planIds query-params))
        params (->> (assoc query-params :planIds plan-ids)
                    (reduce (fn [acc [k v]]
                              (assoc acc k (condp = v
                                             "true" true
                                             "false" false
                                             "null" false
                                             v))) {}))]
    (try
      (ring/response {:success true
                      :quote (manager/get-quote (:id path-params) params)})
      (catch Exception e
        (let [{:keys [:error/code :error/details]} (ex-data e)]
          (if code
            ;; Some user/input issue, respond accordingly
            (-> (ring/bad-request {:success false
                                   :error-code code
                                   :error-details details})
                (ring/status 422))
            ; Some other exception, escalate for general 500
            (throw e)))))))

(defn calculation-status
  [{:keys [path-params]}]
  (let [quote-id (:id path-params)]
    (wrap-log-execution-time
      "/quotes/:id/calculation-status" {:quote-id quote-id}
      (try
        (ring/response {:success true
                        :calculation-complete? (quote-assembly-manager/all-calculations-complete? quote-id)})
        (catch Exception e
          (let [{:keys [:error/code :error/details]} (ex-data e)]
            (if code
              ;; Some user/input issue, respond accordingly
              (-> (ring/bad-request {:success false
                                     :error-code code
                                     :error-details details})
                  (ring/status 422))
              ; Some other exception, escalate for general 500
              (throw e))))))))

(defn post-input-data
  [{:keys [path-params json-params query-params]}]
  (let [quote-id (:id path-params)
        xformed (-> json-params
                    (update
                      ; json-params
                      :claims-specific
                      (fn [m] (map #(update % :claimant-id str) m)))
                    (assoc :source-system "TEMPLATE"))]
    (wrap-log-execution-time
      "/quotes/:id/input-data" {:quote-id quote-id}
      (if (quote-assembly-manager/all-calculations-complete? quote-id)
        (try
          (validate-input! xformed)
          (let [quote (manager/get-quote quote-id {})
                persisted (input-data-manager/create-input-data quote xformed query-params)]
            (ring/created (str "/quotes/" (:id path-params) "/input-data")
                          {:success true
                           :input-data persisted
                           :quote quote}))
          (catch Exception e
            (let [{:keys [:error/code :error/details]} (ex-data e)]
              (if code
                ;; Some user/input issue, respond accordingly
                (-> (ring/bad-request {:success false
                                       :error-code code
                                       :error-details details})
                    (ring/status 422))
                ;; Some other exception, escalate for general 500
                (throw e)))))
        (warn-input-conflict "Could not save input data due to conflict. Calculations in non-complete state for quote." quote-id)))))

(defn put-input-data
  [{:keys [path-params json-params]}]
  (let [quote-id (:id path-params)]
    (wrap-log-execution-time
      "put /quotes/:id/input-data" {:quote-id quote-id}
      (if (quote-assembly-manager/all-calculations-complete? quote-id)
        (try
          (validate-update-input-details! json-params)
          (db/transaction
            (let [sanitized-input (big-decimalize json-params)
                  input-details-update (->> sanitized-input
                                            :details
                                            (input-data-manager/update-input-data! quote-id)
                                            (input-data-manager/decorate-quote-info))
                  enrollment-history-update (input-data-manager/update-enrollment-data! quote-id sanitized-input)
                  aggregate-claims-update (input-data-manager/update-aggregate-claims! quote-id (:claims-aggregate sanitized-input))
                  claims-specific-input (reduce (fn [result claim]
                                                  (assoc result (:id claim) (:claim-details claim)))
                                                {} (:claims-specific sanitized-input))
                  specific-claim-update (input-data-manager/update-specific-claims! claims-specific-input)
                  success? (every? :success [input-details-update enrollment-history-update aggregate-claims-update specific-claim-update])
                  input-response (input-data-manager/structure-input-data-response quote-id)
                  invalid-fields-for-precalculation (input-data-manager/fields-missing-for-precalculations input-response)]
              (when success?
                (if (seq invalid-fields-for-precalculation)
                  (log/debug "Cant precalculate ->" invalid-fields-for-precalculation)
                  (do
                    (input-data-manager/create-input-precalculation {:id quote-id} input-response (-> input-details-update :quote-info :machine-learning-eligibility))
                    (when
                      (some :create-input-version? [input-details-update enrollment-history-update aggregate-claims-update specific-claim-update])
                      (input-data-manager/create-input-version quote-id "INPUT" nil)))))
              (ring/response {:success success?})))
          (catch Exception e
            (let [{:keys [:error/code :error/details]} (ex-data e)]
              (if code
                ;; Some user/input issue, respond accordingly
                (-> (ring/bad-request {:success false
                                       :error-code code
                                       :error-details details})
                    (ring/status 422))
                ;; Some other exception, escalate for general 500
                (throw e)))))
        (warn-input-conflict "Could not update input data due to conflict. Calculations in non-complete state for quote." quote-id)))))

(defn update-input-data-with-quote-info [quote input-data]
  (-> input-data
      (assoc-in [:details :strategic-fee] (:strategic-fee quote))
      (assoc-in [:details :broker-fee] (:broker-fee quote))
      (assoc-in [:details :contract-type] (get-contract-type quote))))

(defn post-manual-input-data
  [{:keys [body path-params query-params]}]
  (let [quote-id (:id path-params)]
    (wrap-log-execution-time
      "post /quotes/:id/manual-input-data" {:quote-id quote-id}
      (if (quote-assembly-manager/all-calculations-complete? quote-id)
        (try
          (let [quote (manager/get-quote quote-id {})
                encoded-bytes (slurp body)
                decoded-bytes (base-64-decode encoded-bytes)
                workbook (with-open [input-stream (io/input-stream decoded-bytes)]
                           (manual-input-file-init/load-workbook input-stream))
                input-data (manual-input-file-init/manual-input-file->input-data workbook)
                input-data-with-quote-info (update-input-data-with-quote-info quote input-data)
                persisted (manual-input-file-manager/create-input-data quote input-data-with-quote-info query-params)]
            (ring/created (str "/quotes/" quote-id "/manual-input-data")
                          {:success true
                           :input-data persisted}))
          (catch Exception e
            (let [{:keys [:error/code :error/details]} (ex-data e)]
              (if code
                ;; Some user/input issue, respond accordingly
                (-> (ring/bad-request {:success false
                                       :error-code code
                                       :error-details details})
                    (ring/status 422))
                ;; Some other exception, escalate for general 500
                (throw e)))))
        (-> {:success false
             :message "Could not update input data via manual input file. Calculations are not complete for this quote."}
            ring/response
            (ring/status 409))))))

(defn post-rate-component-results
  [{:keys [path-params config query-string]}]
  ;; When calculation timeout and retry button is clicked, this will reset the
  ;; existing calculation version and create a new input version so that the
  ;; calculation triggers again.
  (when (and (seq query-string) (re-find #"retry=true" query-string))
    (quote-assembly-manager/mark-calculation-as-timeout (:id path-params))
    (input-data-manager/create-input-version (:id path-params) "INPUT" nil))
  (if (quote-assembly-manager/calculation-timeout? (:id path-params) config)  ; calc timeout, return error
    (-> (ring/bad-request {:success false
                           :error-code :calc/timeout
                           :error-details "Timeout getting calculation status"})
        (ring/status 408))
    (let [process (quote-assembly-manager/orchestrate-calculations (:id path-params) config)
          like-historical-plan-assembly (quote-assembly-manager/assembly-for-like-historical-plan (:id path-params))]
      (ring/response
        {:success true
         :process process
         :like-historical-plan-assembly like-historical-plan-assembly}))))

(defn post-blends-and-adjustments
  [{:keys [path-params json-params]}]
  (ring/response
    {:success true
     :results (quote-assembly-manager/persist-blends-and-adjustments! (:id path-params) json-params)}))

(defn post-adjustments
  [{:keys [path-params json-params]}]
  (ring/response
    {:success true
     :quote-adjustments (quote-assembly-manager/persist-quote-adjustments! (:id path-params) json-params)
     :events (event-manager/add-quote-event (:id path-params) {:event-type "quote.adjustment.save",
                                                               :source     :uw-workbench})
     :assemblies (quote-assembly-manager/persist-assembly-adjustments! json-params)}))

(defn delete-quote
  [{:keys [path-params]}]
  (ring/response
    {:success true
     :results (manager/soft-delete-quote (:id path-params))}))

(defn submit-quote
  [{:keys [path-params config]}]
  (ring/response
    {:success true
     :results (manager/submit-quote (:id path-params) config)}))

(defn apply-idp-data
  [{:keys [path-params json-params query-params config]}]
  (let [quote-id (:id path-params)]
    (wrap-log-execution-time
      "/quotes/:id/external-data" {:quote-id quote-id}
      (if (quote-assembly-manager/all-calculations-complete? quote-id)
        (try
          (ring/response
            {:success true
             :results (idp-manager/apply-idp-data config (:id path-params) json-params query-params)})
          (catch Exception e
            (let [{:keys [:error/code :error/details]} (ex-data e)]
              (if code
                ;; Some user/input issue, respond accordingly
                (-> (ring/bad-request {:success false
                                       :error-code code
                                       :error-details details})
                    (ring/status 422))
                ;; Some other exception, escalate for general 500
                (throw e)))))
        (warn-input-conflict "Could not save input data due to conflict. Calculations in non-complete state for quote." quote-id)))))

(def auth auth-token-interceptor)

(def routes #{["/quotes" :post [auth `post-quote]]
              ["/quotes/:id" :put [auth quote-lock-down-interceptor `put-quote]]
              ["/quotes/:id" :delete [auth quote-lock-down-interceptor `delete-quote]]
              ["/quotes" :get [auth `get-quotes]]
              ["/quotes/:id" :get [auth `get-quote]]
              ["/quotes/:id/submit" :post [auth quote-lock-down-interceptor `submit-quote]]
              ["/quotes/:id/external-data" :post [auth `apply-idp-data]]
              ["/quotes/:id/input-data" :post [auth quote-lock-down-interceptor `post-input-data]]
              ["/quotes/:id/input-data" :put [auth quote-lock-down-interceptor `put-input-data]]
              ["/quotes/:id/manual-input-data" :post [auth quote-lock-down-interceptor `post-manual-input-data]]
              ["/quotes/:id/rate-component-results" :post [auth `post-rate-component-results]]
              ["/quotes/:id/calculation-status" :get [auth `calculation-status]]
              ["/quotes/:id/blends-and-adjustments" :post [auth quote-lock-down-interceptor `post-blends-and-adjustments]]
              ["/quotes/:id/adjustments" :post [auth quote-lock-down-interceptor `post-adjustments]]})
