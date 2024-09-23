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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Repository
public class QuoteStore {

    private static final Logger log = LoggerFactory.getLogger(QuoteStore.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcNamedTemplate;

    private final String findAllQuotesQuery;
    private final String findQuoteByIdQuery ;

    @Autowired
    private ResourceLoader resourceLoader; // To load the JSON file

    public QuoteStore(@Value("${sql.find-all-quotes}") String findAllQuotes,
                      @Value("${sql.find-quote-by-id}")String findQuoteById) {
        this.findAllQuotesQuery = Inquery.readQuery(findAllQuotes);
        this.findQuoteByIdQuery = Inquery.readQuery(findQuoteById);
    }

    // Method to return dummy JSON data from the file
    public List<Quote> findAllDummyQuotes() {
        Resource resource = resourceLoader.getResource("classpath:dummy/all-quotes.json");
        try (InputStream inputStream = resource.getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            // Deserialize JSON content to a list of Quote objects
            return objectMapper.readValue(inputStream, new TypeReference<List<Quote>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load dummy quotes", e);
        }
    }

    public List<Quote> findAllQuotes() {
        return jdbcNamedTemplate.query(findAllQuotesQuery,
                                       new QuoteListExtractor());
    }

    public Quote findDummyQuoteById(Integer id) {
        // Load the JSON file from the classpath
        Resource resource = resourceLoader.getResource("classpath:dummy/dummy-quote.json");
        try (InputStream inputStream = resource.getInputStream()) {
            // Use ObjectMapper to deserialize JSON into a Quote object
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStream, Quote.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load dummy quote", e);
        }
    }

    public Quote findQuoteById(Integer Id) {
        // Create a parameter source and bind the id
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", Id);
        return jdbcNamedTemplate.query(findQuoteByIdQuery,
                                       namedParameters,
                                       new QuoteExtractor());
    }
}

