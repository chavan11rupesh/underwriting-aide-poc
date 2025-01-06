package com.example.underwritingaidepoc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class CreateInputData {

    private final InputService inputService;
    private final QuoteService quoteService;
    private final DestructiveService destructiveService;
    private final LogService logService;

    @Autowired
    public CreateInputData(InputService inputService, QuoteService quoteService, DestructiveService destructiveService, LogService logService) {
        this.inputService = inputService;
        this.quoteService = quoteService;
        this.destructiveService = destructiveService;
        this.logService = logService;
    }

    public InputResponse createInputData(Quote quote, Input input, @RequestParam Map<String, String> queryParameters) {
        Long quoteId = quote.getId();
        Input cleanedInput = decorateQuoteInfo(input);

        if (destructiveService.isDestructive(queryParameters)) {
            destructiveService.deleteInputForQuote(quoteId);
        }

        Input createdInput = inputService.createInputForQuote(quote, cleanedInput);
        maybeUpdateQuote(quoteId, cleanedInput);
        InputResponse inputResponse = inputService.structureInputDataResponse(quoteId);
        List<String> fieldsMissingForPrecalculations = inputService.fieldsMissingForPrecalculations(inputResponse);

        if (!fieldsMissingForPrecalculations.isEmpty()) {
            logService.info("Can't run precalculations - missing required data: {}", fieldsMissingForPrecalculations);
        } else {
            inputService.createInputPreCalculation(quote, inputResponse, cleanedInput.getQuoteInfo().getMachineLearningEligibility());
        }

        return inputResponse;
    }

    private void maybeUpdateQuote(Long quoteId, Input cleanedInput) {
        inputService.maybeUpdateQuote(quoteId, cleanedInput);
    }

    private Input decorateQuoteInfo(Input input) {
        // Implement decorateQuoteInfo logic here
        return input;
    }
}
