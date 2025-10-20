package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.from.domain;

import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.OrderCancel;
import quickfix.field.*;
import quickfix.fix50sp2.OrderCancelRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderCancelRequestFactory {
    public OrderCancelRequest fromDomain(OrderCancel domainOrderCancel) {
        OrderCancelRequest orderCancelRequest = new OrderCancelRequest();
        orderCancelRequest.set(new ClOrdID(UUID.randomUUID().toString()));
        orderCancelRequest.set(new OrigClOrdID(domainOrderCancel.getOrigClOrdID()));
        OrderCancelRequest.NoPartyIDs partyIDGroup = new OrderCancelRequest.NoPartyIDs();
        partyIDGroup.set(new PartyID(domainOrderCancel.getClientID()));
        partyIDGroup.set(new PartyRole(PartyRole.CLIENT_ID));
        orderCancelRequest.addGroup(partyIDGroup);
        orderCancelRequest.set(new Symbol(domainOrderCancel.getSymbol()));
        orderCancelRequest.set(new OrderQty(Double.parseDouble(domainOrderCancel.getQty())));
        // map the received message values
        switch (domainOrderCancel.getSide()) {
            case "BUY":
                orderCancelRequest.set(new Side(Side.BUY));
                break;
            case "SELL":
                orderCancelRequest.set(new Side(Side.SELL));
                break;
        }
        orderCancelRequest.set(new TransactTime(LocalDateTime.now()));
        return orderCancelRequest;
    }
}
