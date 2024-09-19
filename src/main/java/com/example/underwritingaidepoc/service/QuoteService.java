package com.example.underwritingaidepoc.service;

import com.example.underwritingaidepoc.model.Quote;

import java.util.List;

public interface QuoteService {

    List<Quote> findAllQuotes();

    Quote findQuoteById(Integer id);
}
