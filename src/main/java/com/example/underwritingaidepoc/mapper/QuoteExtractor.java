package com.example.underwritingaidepoc.mapper;

import com.example.underwritingaidepoc.model.Quote;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuoteExtractor implements ResultSetExtractor<Quote> {

    public static final String ID = "id";


    @Override
    public Quote extractData(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Quote.builder()
                    .id(Integer.valueOf(resultSet.getString(ID)))
                    .build();
        }
        return null;
    }
}
