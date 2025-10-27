package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class ExecutionReport extends Message {
    private String symbol;
    private String orderID;
    private String clientOrderID;
    private String originalClientOrderID;
    private String clientID;  // from the Party that was received in an execution report
    private String execID;
    private String execType;
    private String ordStatus;
    private String ordRejReason;
    private String side;
    private String orderQty;
    private String price;
    private String timeInForce;
    private String lastQty;
    private String lastPx;
    private String leavesQty;
    private String cumQty;
    private String avgPx;
    private String tradeDate;
    private String transactTime;
    private String text;
}
