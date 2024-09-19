package com.example.underwritingaidepoc.controller;

import com.example.underwritingaidepoc.model.Quote;
import com.example.underwritingaidepoc.service.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
//@RequestMapping("/quotes")
public class QuoteController {

    @Autowired
    QuoteService quoteService;

    private static final Logger logger = LoggerFactory.getLogger(QuoteController.class);

    @GetMapping(path = "/")
    public ResponseEntity<?> findAllQuotes(@RequestParam Map<String, String> queryParams) {

        var quotes = quoteService.findAllQuotes();
        return new ResponseEntity<>(quotes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quote> findQuoteById(@PathVariable Integer id, @RequestParam Map<String, String> queryParams) {

        var quote = quoteService.findQuoteById(id);
        return new ResponseEntity<>(quote, HttpStatus.OK);
    }
}