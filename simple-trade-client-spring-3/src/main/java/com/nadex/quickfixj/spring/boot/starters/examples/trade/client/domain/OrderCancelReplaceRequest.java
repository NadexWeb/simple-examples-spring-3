package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCancelReplaceRequest extends Message {
    private String origClOrdID;
    private String clientID;
    private String symbol;
    private String side; // String so verbose values can be used in UI
    private BigDecimal qty;
    private String ordType; // String so verbose values can be used in UI
    private BigDecimal price;
    private String tif; // String so verbose values can be used in UI
}
