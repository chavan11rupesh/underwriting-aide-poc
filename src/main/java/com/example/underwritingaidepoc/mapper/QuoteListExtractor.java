package com.example.underwritingaidepoc.mapper;

import com.example.underwritingaidepoc.constants.QueryConstants;
import com.example.underwritingaidepoc.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QuoteListExtractor implements ResultSetExtractor<List<Quote>> {

    private static final Logger log = LoggerFactory.getLogger(QuoteListExtractor.class);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Quote mapQuote(ResultSet resultSet) throws SQLException {
        return Quote.builder()
                .id(Integer.valueOf(resultSet.getString(QueryConstants.ID)))
                .employerId(Integer.valueOf(resultSet.getString(QueryConstants.EMPLOYER_ID)))
                .name(resultSet.getString(QueryConstants.NAME))
                .owner(resultSet.getString(QueryConstants.OWNER))
                .network(resultSet.getString(QueryConstants.NETWORK))
                //.status(resultSet.getString(QueryConstants.STATUS))
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

    @Override
    public List<Quote> extractData(ResultSet resultSet) {

        List<Quote> quoteList = new ArrayList<>();

        try {
            while(resultSet.next()){
                Quote quote = mapQuote(resultSet);
                quoteList.add(quote);
            }
        }
        catch(Exception ex) {
            log.error("could not map Quote: ", ex);
            throw new RuntimeException("could not map Quote. ", ex);
        }

        return quoteList;
    }
}
