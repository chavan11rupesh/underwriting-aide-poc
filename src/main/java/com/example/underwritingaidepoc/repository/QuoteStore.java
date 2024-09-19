package com.example.underwritingaidepoc.repository;

import com.example.underwritingaidepoc.mapper.QuoteExtractor;
import com.example.underwritingaidepoc.mapper.QuoteListExtractor;
import com.example.underwritingaidepoc.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class QuoteStore {

    private static final Logger log = LoggerFactory.getLogger(QuoteStore.class);


    @Autowired
    private NamedParameterJdbcTemplate jdbcNamedTemplate;

    String findAllQuotesQuery = "select * from underwriting.quote";
    String findQuoteByIdQuery = "select * from underwriting.quote where id = 1";


    public List<Quote> findAllQuotes() {
        return jdbcNamedTemplate.query(findAllQuotesQuery,
                                       new QuoteListExtractor());
    }


    public Quote findQuoteById(Integer Id) {
        return jdbcNamedTemplate.query(findQuoteByIdQuery,
                                       new QuoteExtractor());
    }
}

