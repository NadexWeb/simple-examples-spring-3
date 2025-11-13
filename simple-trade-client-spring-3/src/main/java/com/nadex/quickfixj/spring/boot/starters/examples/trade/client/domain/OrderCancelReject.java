package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class OrderCancelReject extends Message {
    private String orderID;
    private String clientOrderID;
    private Character ordStatus;
    private Character cxlRejResponseTo;
    private Integer cxlRejReason;
    private String text;
}
