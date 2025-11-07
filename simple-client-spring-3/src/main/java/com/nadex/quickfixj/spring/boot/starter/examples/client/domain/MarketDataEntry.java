package com.nadex.quickfixj.spring.boot.starter.examples.client.domain;

import lombok.Data;

@Data
public class MarketDataEntry {
    private String mdEntryType;
    private String mdEntryPx;
    private String mdEntrySize;
}
