package com.example.underwritingaidepoc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Quote {

    Integer id;
    Integer employerId;
    String name;
    String riskReviewerName;
    String riskReviewerRole;
    String riskReviewerIdentifier;
    String owner;
    //String status;
    String inputSource;
    String workflowQuoteId;
    String effectiveDate;
    String preUnderwriterName;
    String preUnderwriterIdentifier;
    String underwriterAssignedIdentifier;
    String network;
    String lastUpdated;
    String rateType;
    String brokerFee;
    String dateCreated;
    String changeRequestReason;
    String isDeleted;
    String underwriterAssignedRole;
    String strategicFee;
    String contractTypeMonthsIncurredIn;
    String salesRepresentativeName;
    String fundingArrangement;
    String channelPartnerStatus;
    String dateDeleted;
    String underwriterAssignedName;
    String contractTypeMonthsPaidIn;



}
