package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.from.fix;

import quickfix.FieldNotFound;
import quickfix.field.MsgType;
import quickfix.fix50sp2.BusinessMessageReject;

public class BusinessMessageRejectFactory {
    /**
     * Returns a Domain value object from the FIX BusinessMessageReject
     * @param businessMessageReject  FIX BusinessMessageReject
     * @return Domain Business Message Reject
     * @throws FieldNotFound QuickFIX/J Exception
     */
    public static com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.BusinessMessageReject fromFix(BusinessMessageReject businessMessageReject) throws FieldNotFound {
        com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.BusinessMessageReject domainBusinessMessageReject =
                new com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.BusinessMessageReject();
        domainBusinessMessageReject.setMsgType(businessMessageReject.getHeader().getString(MsgType.FIELD));
        if (businessMessageReject.isSetRefSeqNum()) {
            domainBusinessMessageReject.setRefSeqNum(businessMessageReject.getRefSeqNum().getValue());
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
