package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class OrderCancel extends Message {
    public String origClOrdID;
    public String clientID;
    public String symbol;
    public String side;
    public String qty;
}
