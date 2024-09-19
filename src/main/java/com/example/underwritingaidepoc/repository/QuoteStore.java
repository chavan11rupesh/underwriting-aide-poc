package com.example.underwritingaidepoc.repository;

import com.example.underwritingaidepoc.mapper.QuoteExtractor;
import com.example.underwritingaidepoc.model.Quote;
import lombok.Value;
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


    public List<Quote> findAllQuotes() {
        return Collections.singletonList(jdbcNamedTemplate.query(findAllQuotesQuery,
                new QuoteExtractor()));
    }


    public Quote findQuoteById(Integer Id) {
        return jdbcNamedTemplate.query("select * from quote where id = 1",
                new QuoteExtractor());
    }
}

