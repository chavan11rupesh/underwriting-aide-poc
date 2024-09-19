package com.example.underwritingaidepoc.service;

import com.example.underwritingaidepoc.model.Quote;
import com.example.underwritingaidepoc.repository.QuoteStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuoteService {

    @Autowired
    QuoteStore quoteStore;

    public List<Quote> findAllQuotes() {
        return quoteStore.findAllQuotes();

    }

    public Quote findQuoteById(Integer Id) {
        return quoteStore.findQuoteById(Id);

    }

}
