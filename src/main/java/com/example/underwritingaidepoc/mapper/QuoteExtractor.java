package com.example.underwritingaidepoc.mapper;

import com.example.underwritingaidepoc.constants.QueryConstants;
import com.example.underwritingaidepoc.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;

public class QuoteExtractor implements ResultSetExtractor<Quote> {

    private static final Logger log = LoggerFactory.getLogger(QuoteExtractor.class);

    @Override
    public Quote extractData(ResultSet resultSet) {
        try {
            if (resultSet.next()) {

                return Quote.builder()
                        .id(Integer.valueOf(resultSet.getString(QueryConstants.ID)))
                        .employerId(Integer.valueOf(resultSet.getString(QueryConstants.EMPLOYER_ID)))
                        .name(resultSet.getString(QueryConstants.NAME))
                        .owner(resultSet.getString(QueryConstants.OWNER))
                        .network(resultSet.getString(QueryConstants.NETWORK))
                        .rateType(resultSet.getString(QueryConstants.RATE_TYPE))
                        .brokerFee(resultSet.getString(QueryConstants.BROKER_FEE))
                        .isDeleted(resultSet.getString(QueryConstants.IS_DELETED))
                        .inputSource(resultSet.getString(QueryConstants.INPUT_SOURCE))
                        .dateDeleted(resultSet.getString(QueryConstants.DATE_DELETED))
                        .lastUpdated(resultSet.getString(QueryConstants.LAST_UPDATED))
                        .dateCreated(resultSet.getString(QueryConstants.DATE_CREATED))
                        .effectiveDate(resultSet.getString(QueryConstants.EFFECTIVE_DATE))
                        .strategicFee(resultSet.getString(QueryConstants.STRATEGIC_FEE))
                        .workflowQuoteId(resultSet.getString(QueryConstants.WORKFLOW_QUOTE_ID))
                        .changeRequestReason(resultSet.getString(QueryConstants.CHANGE_REQUEST_REASON))
                        .salesRepresentativeName(resultSet.getString(QueryConstants.SALES_REPRESENTATIVE_NAME))
                        .fundingArrangement(resultSet.getString(QueryConstants.FUNDING_ARRANGEMENT))
                        .channelPartnerStatus(resultSet.getString(QueryConstants.CHANNEL_PARTNER_STATUS))
                        .riskReviewerName(resultSet.getString(QueryConstants.RISK_REVIEWER_NAME))
                        .riskReviewerRole(resultSet.getString(QueryConstants.RISK_REVIEWER_ROLE))
                        .riskReviewerIdentifier(resultSet.getString(QueryConstants.RISK_REVIEWER_IDENTIFIER))
                        .preUnderwriterName(resultSet.getString(QueryConstants.PRE_UNDERWRITER_NAME))
                        .preUnderwriterIdentifier(resultSet.getString(QueryConstants.PRE_UNDERWRITER_IDENTIFIER))
                        .underwriterAssignedName(resultSet.getString(QueryConstants.UNDERWRITER_ASSIGNED_NAME))
                        .underwriterAssignedRole(resultSet.getString(QueryConstants.UNDERWRITER_ASSIGNED_ROLE))
                        .underwriterAssignedIdentifier(resultSet.getString(QueryConstants.UNDERWRITER_ASSIGNED_IDENTIFIER))
                        .contractTypeMonthsPaidIn(resultSet.getString(QueryConstants.CONTRACT_TYPE_MONTHS_PAID_IN))
                        .contractTypeMonthsIncurredIn(resultSet.getString(QueryConstants.CONTRACT_TYPE_MONTHS_INCURRED_IN))
                        .build();
            }
        }
        catch(Exception ex) {
            log.error("could not extract Quote: ", ex);
            throw new RuntimeException("could not extract Quote. ", ex);
        }

        return null;
    }
}
