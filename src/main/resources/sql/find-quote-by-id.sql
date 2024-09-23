WITH LatestEvent AS (
    SELECT 
        quote_id, 
        source, 
        event_type,
        ROW_NUMBER() OVER (PARTITION BY quote_id ORDER BY event_datetime DESC) AS rn
    FROM 
        quote_event
)
SELECT 
    q.*,
    COALESCE(le.source, wq.source) AS source,
    e.name AS employer_name,
    e.external_id, 
    le.event_type AS status
FROM 
    quote AS q
LEFT JOIN 
    workflow_quote AS wq ON q.workflow_quote_id = wq.id
LEFT JOIN 
    employer AS e ON q.employer_id = e.id
LEFT JOIN 
    LatestEvent AS le ON q.id = le.quote_id AND le.rn = 1
WHERE 
    q.id = 143;
