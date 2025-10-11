package com.nadex.quickfixj.spring.boot.starters.examples.trade.client;

import org.springframework.stereotype.Component;
import quickfix.field.*;
import quickfix.fix50sp2.NewOrderSingle;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.UUID;

public class NewOrderSingleFactory {
    public static NewOrderSingle fromDomainNewOrderSingle(com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.NewOrderSingle domainNewOrderSingle) {
        NewOrderSingle fixNewOrderSingle =  new NewOrderSingle();

        fixNewOrderSingle.set(new Symbol(domainNewOrderSingle.getSymbol()));
        fixNewOrderSingle.set(new ClOrdID(UUID.randomUUID().toString()));
        fixNewOrderSingle.set(new OrderQty(Double.parseDouble(domainNewOrderSingle.getQty())));
        fixNewOrderSingle.set(new Price(Double.parseDouble(domainNewOrderSingle.getPx())));
                // Add the Party Group
        NewOrderSingle.NoPartyIDs partyIDGroup = new NewOrderSingle.NoPartyIDs();
        partyIDGroup.set(new PartyID(domainNewOrderSingle.getClientID()));
        partyIDGroup.set(new PartyRole(PartyRole.CLIENT_ID));
        fixNewOrderSingle.addGroup(partyIDGroup);

        // map the received message values
        switch (domainNewOrderSingle.getSide()) {
            case "BUY":
                fixNewOrderSingle.set(new Side(Side.BUY));
                break;
            case "SELL":
                fixNewOrderSingle.set(new Side(Side.SELL));
                break;
        }
        switch (domainNewOrderSingle.getTif()) {
            case "IOC":
                fixNewOrderSingle.set(new TimeInForce(TimeInForce.IMMEDIATE_OR_CANCEL));
                break;
            case "FOK":
                fixNewOrderSingle.set(new TimeInForce(TimeInForce.FILL_OR_KILL));
                break;
            case "GTC":
                fixNewOrderSingle.set(new TimeInForce(TimeInForce.GOOD_TILL_CANCEL));
                break;
        }
        switch (domainNewOrderSingle.getOrdType()) {
            case "LIMIT":
                fixNewOrderSingle.set(new OrdType(OrdType.LIMIT));
                break;
            case "STOP_LIMIT":
                fixNewOrderSingle.set(new OrdType(OrdType.STOP_LIMIT));
                break;
        }
        fixNewOrderSingle.set(new TransactTime(LocalDateTime.now()));
        return fixNewOrderSingle;
    }
}
