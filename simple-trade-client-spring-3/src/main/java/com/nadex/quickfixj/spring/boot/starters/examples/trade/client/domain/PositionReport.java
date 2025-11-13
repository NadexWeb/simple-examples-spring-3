package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PositionReport extends Message {
    private String posMaintRptID;
    private String posReqID;
    private Integer posReqType;
    private String posReqResult;
    private String clearingBusinessDate;
    private List<Party> parties;
    private String symbol;
    private String maturityMonthYear;
    private String maturityDay;
    private BigDecimal settlPrice;
    private Integer settlPriceType;
    private String resettleIndicator;
    private String subscriptionRequestType;
    private Integer totalNumPosReports;

    private List<Underlying> underlyings = new ArrayList<>();
    private List<PositionQty> positionQtys = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class Underlying {
        private String underlyingSymbol;
        private BigDecimal underlyingSettlPrice;
        private Integer underlyingSettlPriceType;
    }

    @Data
    @AllArgsConstructor
    public static class PositionQty {
        private String posType;
        private BigDecimal longQty;
        private BigDecimal shortQty;
        private Integer posQtyStatus;
    }
}
