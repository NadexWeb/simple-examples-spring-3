package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PositionReport {
    private String posMaintRptID;
    private String posReqID;
    private int posReqType;
    private String posReqResult;
    private String clearingBusinessDate;
    private List<Party> parties;
    private String symbol;
    private String maturityMonthYear;
    private String maturityDay;
    private String settlPrice;
    private int settlPriceType;
    private String resettleIndicator;
    private String subscriptionRequestType;
    private int totalNumPosReports;

    private List<Underlying> underlyings = new ArrayList<>();
    private List<PositionQty> positionQtys = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class Underlying {
        private String underlyingSettlPrice;
        private int underlyingSettlPriceType;
    }

    @Data
    @AllArgsConstructor
    public static class PositionQty {
        private String posType;
        private long longQty;
        private long shortQty;
        private int posQtyStatus;
    }
}
