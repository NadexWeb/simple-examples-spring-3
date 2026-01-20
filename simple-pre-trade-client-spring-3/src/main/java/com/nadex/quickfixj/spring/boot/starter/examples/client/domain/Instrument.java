package com.nadex.quickfixj.spring.boot.starter.examples.client.domain;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class Instrument {
    private String symbol;
    private String product;
    private String securitySubType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private double minPriceIncrement;
}
