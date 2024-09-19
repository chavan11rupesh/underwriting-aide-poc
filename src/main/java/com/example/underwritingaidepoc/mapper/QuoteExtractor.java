package com.example.underwritingaidepoc.mapper;

import com.example.underwritingaidepoc.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;

public class QuoteExtractor implements ResultSetExtractor<Quote> {

    private static final Logger log = LoggerFactory.getLogger(QuoteExtractor.class);

    public static final String ID = "id";
    public static final String EMPLOYER_ID = "employer_id";
    public static final String NAME = "name";


    @Override
    public Quote extractData(ResultSet resultSet) {
        try {
            if (resultSet.next()) {

                return Quote.builder()
                        .id(Integer.valueOf(resultSet.getString(ID)))
                        .employerId(Integer.valueOf(resultSet.getString(EMPLOYER_ID)))
                        .name(resultSet.getString(NAME))
                        .build();
            }
        }
        catch(Exception ex) {
            log.error("could not extract Quote: ", ex);
            throw new RuntimeException("could not extract Quote. ", ex);
        }

        return null;
    }
}
