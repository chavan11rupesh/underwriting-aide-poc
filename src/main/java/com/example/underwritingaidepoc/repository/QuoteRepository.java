package com.example.underwritingaidepoc.repository;

import com.example.underwritingaidepoc.model.Quote;
import com.example.underwritingaidepoc.model.QuoteList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    List<Quote> findAllQuotes();

    Quote findQuoteById(Long id);

}
