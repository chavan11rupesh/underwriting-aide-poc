package com.example.underwritingaidepoc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InputService {

    private final InputRepository inputRepository;
    private final QuoteRepository quoteRepository;
    private final DestructiveService destructiveService;
    private final LogService logService;

    @Autowired
    public InputService(InputRepository inputRepository, QuoteRepository quoteRepository, DestructiveService destructiveService, LogService logService) {
        this.inputRepository = inputRepository;
        this.quoteRepository = quoteRepository;
        this.destructiveService = destructiveService;
        this.logService = logService;
    }

    public Input createInputForQuote(Quote quote, Input input) {
        Long quoteId = quote.getId();
        String sourceReference = input.getSourceReference();
        InputDetails inputDetails = createInputDetails(quoteId, input);

        // Reset aggregate attachment point, if existing
        resetAggAttPoint(quoteId, inputDetails);

        // Compensate for quotes we received before importing effective date from SF
        if (quote.getEffectiveDate() == null) {
            Quote updatedQuote = new Quote();
            updatedQuote.setId(quoteId);
            updatedQuote.setEffectiveDate(input.getDetails().getEffectiveDate());
            quoteRepository.save(updatedQuote);
        }

        createInputDetailsSuggestions(quoteId, inputDetails, input.getDetailsSuggestions());
        createInputEnrollmentHistory(quoteId, input);
        createInputClaimsAggregate(quoteId, input);
        createInputClaimsSpecific(quoteId, input);
        createInputCensus(quoteId, input);
        createInputVersion(quoteId, input.getSourceSystem(), sourceReference);

        return input;
    }

    private InputDetails createInputDetails(Long quoteId, Input input) {
        InputDetails details = new InputDetails();
        details.setQuoteId(quoteId);
        details.setEffectiveYear(2024);
        details.setDetails(input.getDetails());
        return inputRepository.save(details);
    }

    // Implement other methods like createInputDetailsSuggestions, createInputEnrollmentHistory, etc.
}

