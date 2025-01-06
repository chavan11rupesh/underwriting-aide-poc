package com.example.underwritingaidepoc.controller;

import com.example.underwritingaidepoc.model.Quote;
import com.example.underwritingaidepoc.service.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class QuoteController {

    @Autowired
    QuoteService quoteService;

    @Autowired
    private QuoteAssemblyManager quoteAssemblyManager;

    @Autowired
    private InputDataManager inputDataManager;

    @Autowired
    private QuoteManager quoteManager;

    @Autowired
    private InputDataValidator inputDataValidator;

    private static final Logger logger = LoggerFactory.getLogger(QuoteController.class);

    @GetMapping(path = "/")
    public ResponseEntity<?> findAllQuotes(@RequestParam Map<String, String> queryParams) {


        var quotes = quoteService.findAllQuotes();
        return new ResponseEntity<>(quotes, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Quote> findQuoteById(@PathVariable Integer id, @RequestParam Map<String, String> queryParams) {

        var quote = quoteService.findQuoteById(id);
        return new ResponseEntity<>(quote, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> postInputData(@PathVariable("id") Integer quoteId, @RequestBody InputDataRequest inputDataRequest, @RequestParam Map<String, String> queryParameters) {
        try {
            // Validate input data
            inputDataValidator.validate(inputDataRequest);

            // Get quote and create input data
            Quote quote = quoteService.findQuoteById(quoteId);
            InputData inputData = inputDataManager.createInputData(quote, inputDataRequest, queryParameters);

            // Return success response
            return ResponseEntity.created("/quotes/" + quoteId + "/input-data").body(
                    new InputDataResponse(true, inputData, quote));
        } catch (Exception e) {
            // Handle validation errors
            if (e instanceof InputDataValidationException) {
                InputDataValidationException validationException = (InputDataValidationException) e;
                return ResponseEntity.badRequest().body(
                        new InputDataResponse(false, null, null, validationException.getErrorCode(), validationException.getDetails()));
            } else {
                // Handle other exceptions
                throw e;
            }
        }
    }


}