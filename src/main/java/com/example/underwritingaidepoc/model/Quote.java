package com.example.underwritingaidepoc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    String inputSource;
    String workflowQuoteId;
    LocalDate effectiveDate;
    LocalDateTime dateDeleted;
    LocalDateTime lastUpdated;
    LocalDateTime dateCreated;
    String preUnderwriterName;
    String preUnderwriterIdentifier;
    String underwriterAssignedIdentifier;
    String network;
    String rateType;
    String brokerFee;
    String changeRequestReason;
    String isDeleted;
    String underwriterAssignedRole;
    String strategicFee;
    String contractTypeMonthsIncurredIn;
    String salesRepresentativeName;
    String fundingArrangement;
    String channelPartnerStatus;
    String underwriterAssignedName;
    String contractTypeMonthsPaidIn;
    String employerName;
    String externalId;
    String status;
    String source;

}
