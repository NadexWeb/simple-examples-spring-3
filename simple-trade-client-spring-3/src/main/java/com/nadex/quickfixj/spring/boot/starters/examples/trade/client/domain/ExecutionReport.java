package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExecutionReport extends Message {
    private String symbol;
    private String orderID;
    private String clientOrderID;
    private String originalClientOrderID;
    private String clientID;  // from the Party that was received in an execution report
    private String execID;
    private Character execType;
    private Character ordStatus;
    private Integer   ordRejReason;
    private Character side;
    private BigDecimal orderQty;
    private BigDecimal price;
    private Character timeInForce;
    private BigDecimal lastQty;
    private BigDecimal lastPx;
    private BigDecimal leavesQty;
    private BigDecimal cumQty;
    private BigDecimal avgPx;
    private String tradeDate;
    private String transactTime;
    private String text;
}
