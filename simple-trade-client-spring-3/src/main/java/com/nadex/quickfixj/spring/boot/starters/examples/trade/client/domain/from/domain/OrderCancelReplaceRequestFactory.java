package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.from.domain;

import quickfix.field.*;
import quickfix.fix50sp2.OrderCancelReplaceRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderCancelReplaceRequestFactory {
    public OrderCancelReplaceRequest fromDomain(com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.OrderCancelReplaceRequest domainOrderCancelReplaceRequest) {
        OrderCancelReplaceRequest orderCancelReplaceRequest = new OrderCancelReplaceRequest();
        orderCancelReplaceRequest.set(new ClOrdID(UUID.randomUUID().toString()));
        orderCancelReplaceRequest.set(new OrigClOrdID(domainOrderCancelReplaceRequest.getOrigClOrdID()));
        OrderCancelReplaceRequest.NoPartyIDs partyIDGroup = new OrderCancelReplaceRequest.NoPartyIDs();
        partyIDGroup.set(new PartyID(domainOrderCancelReplaceRequest.getClientID()));
        partyIDGroup.set(new PartyRole(PartyRole.CLIENT_ID));
        orderCancelReplaceRequest.addGroup(partyIDGroup);
        orderCancelReplaceRequest.set(new Symbol(domainOrderCancelReplaceRequest.getSymbol()));
        orderCancelReplaceRequest.set(new OrderQty(new BigDecimal(domainOrderCancelReplaceRequest.getQty())));
        orderCancelReplaceRequest.set(new Price(new BigDecimal(domainOrderCancelReplaceRequest.getPx())));
        // map the received message values
        switch (domainOrderCancelReplaceRequest.getSide()) {
            case "BUY":
                orderCancelReplaceRequest.set(new Side(Side.BUY));
                break;
            case "SELL":
                orderCancelReplaceRequest.set(new Side(Side.SELL));
                break;
        }
        switch (domainOrderCancelReplaceRequest.getTif()) {
            case "IOC":
                orderCancelReplaceRequest.set(new TimeInForce(TimeInForce.IMMEDIATE_OR_CANCEL));
                break;
            case "FOK":
                orderCancelReplaceRequest.set(new TimeInForce(TimeInForce.FILL_OR_KILL));
                break;
            case "GTC":
                orderCancelReplaceRequest.set(new TimeInForce(TimeInForce.GOOD_TILL_CANCEL));
                break;
        }
        switch (domainOrderCancelReplaceRequest.getOrdType()) {
            case "LIMIT":
                orderCancelReplaceRequest.set(new OrdType(OrdType.LIMIT));
                break;
            case "STOP_LIMIT":
                orderCancelReplaceRequest.set(new OrdType(OrdType.STOP_LIMIT));
                break;
        }
        orderCancelReplaceRequest.set(new TransactTime(LocalDateTime.now()));
        return orderCancelReplaceRequest;
    }
}
