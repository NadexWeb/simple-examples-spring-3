package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.from.fix;

import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.OrderCancelReject;
import quickfix.FieldNotFound;
import quickfix.field.MsgType;

public class OrderCancelRejectFactory {
    /**
     * Returns a Domain value object from the FIX Execution Report
     * @param orderCancelReject FIX OrderCancelReject
     * @return Domain OrderCancelReject
     * @throws FieldNotFound QuickFIX/J Exception
     */
    public static OrderCancelReject fromFix(quickfix.fix50sp2.OrderCancelReject orderCancelReject) throws FieldNotFound {
        OrderCancelReject domainOrderCancelReject = new OrderCancelReject();
        domainOrderCancelReject.setMsgType(orderCancelReject.getHeader().getString(MsgType.FIELD));
        if (orderCancelReject.isSetOrderID()) {
            domainOrderCancelReject.setOrderID(orderCancelReject.getOrderID().getValue());
        }
        if (orderCancelReject.isSetClOrdID()) {
            domainOrderCancelReject.setClientOrderID(orderCancelReject.getClOrdID().getValue());
        }
        if (orderCancelReject.isSetOrdStatus()) {
            domainOrderCancelReject.setOrdStatus(orderCancelReject.getOrdStatus().getValue());
        }
        if (orderCancelReject.isSetCxlRejResponseTo()) {
            domainOrderCancelReject.setCxlRejResponseTo(orderCancelReject.getCxlRejResponseTo().getValue());
        }
        if (orderCancelReject.isSetCxlRejReason()) {
            domainOrderCancelReject.setCxlRejReason(orderCancelReject.getCxlRejReason().getValue());
        }
        if (orderCancelReject.isSetText()) {
            domainOrderCancelReject.setText(orderCancelReject.getText().toString());
        }
        return domainOrderCancelReject;
    }

}
