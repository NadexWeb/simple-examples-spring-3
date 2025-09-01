package com.nadex.quickfixj.spring.boot.starter.examples.server.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Instrument {
    private String symbol;
    private String underlyingSymbol;
    private int product;
    private String period;
    private String securitySubType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private double minPriceIncrement;
    private String currency;
}
