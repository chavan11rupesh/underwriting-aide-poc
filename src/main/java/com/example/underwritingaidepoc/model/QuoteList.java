package com.example.underwritingaidepoc.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class QuoteList {
    List<Quote> quotes;
}

