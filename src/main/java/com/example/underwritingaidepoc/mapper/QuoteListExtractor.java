package com.example.underwritingaidepoc.mapper;

import com.example.underwritingaidepoc.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuoteListExtractor implements ResultSetExtractor<List<Quote>> {

    private static final Logger log = LoggerFactory.getLogger(QuoteListExtractor.class);

    public static final String ID = "id";
    public static final String EMPLOYER_ID = "employer_id";
    public static final String NAME = "name";


    public Quote mapQuote(ResultSet resultSet) throws SQLException {
        return Quote.builder()
                .id(Integer.valueOf(resultSet.getString(ID)))
                .employerId(Integer.valueOf(resultSet.getString(EMPLOYER_ID)))
                .name(resultSet.getString(NAME))
                .build();
    }

    @Override
    public List<Quote> extractData(ResultSet resultSet) {

        List<Quote> quoteList = new ArrayList<>();

        try {
            while(resultSet.next()){
                Quote quote = mapQuote(resultSet);
                quoteList.add(quote);
            }
        }
        catch(Exception ex) {
            log.error("could not map Quote: ", ex);
            throw new RuntimeException("could not map Quote. ", ex);
        }

        return quoteList;
    }
}
