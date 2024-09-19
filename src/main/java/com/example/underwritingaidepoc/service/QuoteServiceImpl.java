package com.example.underwritingaidepoc.service;

import com.example.underwritingaidepoc.model.Quote;
import com.example.underwritingaidepoc.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteServiceImpl implements QuoteService{

    @Autowired
    private QuoteRepository repository;

    @Override
    public List<Quote> findAllQuotes() {
            return repository.findAllQuotes();

    }

    @Override
    public Quote findQuoteById(Integer id) {
        return null;
    }
}

