package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class BusinessMessageReject {
    String refSeqNum;
    String refMsgType;
    String businessRejectRefID;
    String businessRejectReason;
    String text;
}
