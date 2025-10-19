package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class OrderCancelReplaceRequest extends Message {
    public String origClOrdID;
    public String clientID;
    public String symbol;
    public String side;
    public String qty;
    public String ordType;
    public String px;
    public String tif;
}
