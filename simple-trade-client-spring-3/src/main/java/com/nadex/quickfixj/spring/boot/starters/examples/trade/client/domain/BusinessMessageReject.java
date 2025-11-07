package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class BusinessMessageReject extends Message {
    private String refSeqNum;
    private String refMsgType;
    private String businessRejectRefID;
    private String businessRejectReason;
    private String text;
}
