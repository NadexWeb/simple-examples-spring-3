package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class OrderCancelReplaceRequest extends Message {
    private String origClOrdID;
    private String clientID;
    private String symbol;
    private String side;
    private String qty;
    private String ordType;
    private String px;
    private String tif;
}
