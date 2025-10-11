package com.nadex.quickfixj.spring.boot.starters.examples.trade.client;

import quickfix.FieldNotFound;
import quickfix.fix50sp2.BusinessMessageReject;

public class BusinessMessageRejectFactory {
    public static com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.BusinessMessageReject businessMessageRejectFromFix(BusinessMessageReject businessMessageReject) throws FieldNotFound {
        com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.BusinessMessageReject domainBusinessMessageReject =
                new com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.BusinessMessageReject();
        if (businessMessageReject.isSetRefSeqNum()) {
            domainBusinessMessageReject.setRefSeqNum(businessMessageReject.getRefSeqNum().toString());
        }
        if (businessMessageReject.isSetRefMsgType()) {
            domainBusinessMessageReject.setRefMsgType(businessMessageReject.getRefMsgType().getValue());
        }
        if (businessMessageReject.isSetBusinessRejectRefID()) {
            domainBusinessMessageReject.setBusinessRejectRefID(businessMessageReject.getBusinessRejectRefID().getValue());
        }
        if (businessMessageReject.isSetText()) {
            domainBusinessMessageReject.setText(businessMessageReject.getText().getValue());
        }
        return domainBusinessMessageReject;
    }
}
