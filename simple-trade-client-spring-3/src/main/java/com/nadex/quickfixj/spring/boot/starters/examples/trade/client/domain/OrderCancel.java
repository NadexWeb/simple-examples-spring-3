package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCancel extends Message {
    private String origClOrdID;
    private String clientID;
    private String symbol;
    private String side;  // String so verbose values can be used in UI
    private BigDecimal qty;
}
