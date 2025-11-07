package com.nadex.quickfixj.spring.boot.starter.examples.client.domain;


import lombok.Data;

import java.util.List;

@Data
public class MarketDataSnapshot  extends Message {
    private String symbol;
    private String mdReqId;
    private String marketDepth;
    private Integer noMmEntries;
    private MarketDataEntry bid; // 0
    private MarketDataEntry offer; // 1
}
