package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class ExecutionReport {
    public String symbol;
    public String orderID;
    public String clientOrderID;
    public String execID;
    public String execType;
    public String ordStatus;
    public String ordRejReason;
    public String side;
    public String orderQty;
    public String price;
    public String timeInForce;
    public String lastQty;
    public String lastPx;
    public String leavesQty;
    public String cumQty;
    public String avgPx;
    public String tradeDate;
    public String transactTime;
    public String text;
}
