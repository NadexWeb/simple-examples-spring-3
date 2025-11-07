package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class OrderCancel extends Message {
    private String origClOrdID;
    private String clientID;
    private String symbol;
    private String side;
    private String qty;
}
