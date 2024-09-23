package com.example.underwritingaidepoc.repository;

import com.example.underwritingaidepoc.mapper.QuoteExtractor;
import com.example.underwritingaidepoc.mapper.QuoteListExtractor;
import com.example.underwritingaidepoc.model.Quote;

import com.example.underwritingaidepoc.utilities.Inquery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Repository
public class QuoteStore {

    private static final Logger log = LoggerFactory.getLogger(QuoteStore.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcNamedTemplate;

    private final String findAllQuotesQuery;
    private final String findQuoteByIdQuery ;

    public QuoteStore(@Value("${sql.find-all-quotes}") String findAllQuotes,
                      @Value("${sql.find-quote-by-id}")String findQuoteById) {
        this.findAllQuotesQuery = Inquery.readQuery(findAllQuotes);
        this.findQuoteByIdQuery = Inquery.readQuery(findQuoteById);
    }

    public List<Quote> findAllQuotes() {
        return jdbcNamedTemplate.query(findAllQuotesQuery,
                                       new QuoteListExtractor());
    }

    public Quote findQuoteById(Integer Id) {
        // Create a parameter source and bind the id
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", Id);
        return jdbcNamedTemplate.query(findQuoteByIdQuery,
                                       namedParameters,
                                       new QuoteExtractor());
    }
}

