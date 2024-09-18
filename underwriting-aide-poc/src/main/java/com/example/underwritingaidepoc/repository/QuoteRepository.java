package com.example.underwritingaidepoc.repository;

import com.example.underwritingaidepoc.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRepository extends JpaRepository<QuoteRepository, Long> {

    List<Quote> findAllQuotes();

    Quote findQuoteById(Long id);

}
