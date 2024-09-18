package com.example.underwritingaidepoc.controller;

import com.example.underwritingaidepoc.service.QuoteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/quotes")
public class QuoteController {

    @Autowired
    private QuoteServiceImpl quoteService;

    private static final Logger logger = LoggerFactory.getLogger(QuoteController.class);

    @GetMapping
    public ResponseEntity<Map<String, Object>> getQuotes(@RequestParam Map<String, String> queryParams) {
        // Handle query parameters and call manager.getQuotes(queryParams)
        return ResponseEntity.ok(Map.of("success", true, "quotes", quoteService.findAllQuotes()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getQuote(@PathVariable Long id, @RequestParam Map<String, String> queryParams) {
        // Call manager.getQuote(id)
        return ResponseEntity.ok(Map.of("success", true, "quote", quoteService.findQuoteById(id)));
    }
}