package com.example.underwritingaidepoc.service;

import java.util.List;
import java.util.Map;

import com.example.underwritingaidepoc.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.underwritingaidepoc.model.Quote;

@Service
public class QuoteServiceImpl implements QuoteService{

    @Autowired
    private QuoteRepository repository;

    @Override
    public List<Quote> findAllQuotes() {
            return repository.findAllQuotes();

    }

    @Override
    public Quote findQuoteById(Long id) {
        return null;
    }
}

