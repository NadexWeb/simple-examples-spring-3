package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class BusinessMessageReject extends Message {
    private Integer refSeqNum;
    private String refMsgType;
    private String businessRejectRefID;
    private Integer businessRejectReason;
    private String text;
}
