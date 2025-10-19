package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class OrderCancelReject extends Message {
    public String orderID;
    public String clientOrderID;
    public String ordStatus;
    public String cxlRejResponseTo;
    public String cxlRejReason;
    public String text;
}
