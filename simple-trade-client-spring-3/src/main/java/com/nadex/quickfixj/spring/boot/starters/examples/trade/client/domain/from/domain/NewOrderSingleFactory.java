package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.from.domain;

import quickfix.field.*;
import quickfix.fix50sp2.NewOrderSingle;

import java.time.LocalDateTime;
import java.util.UUID;

public class NewOrderSingleFactory {
    /**
     * Constructs a FIX New Order Single message the received Domain object
     * @param domainNewOrderSingle Domain New Order Single
     * @return FIX New Order Single
     */
    public static NewOrderSingle fromDomain(com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.NewOrderSingle domainNewOrderSingle) {
        NewOrderSingle fixNewOrderSingle =  new NewOrderSingle();

        fixNewOrderSingle.set(new Symbol(domainNewOrderSingle.getSymbol()));
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
        //generated here
        fixNewOrderSingle.set(new ClOrdID(UUID.randomUUID().toString()));
        fixNewOrderSingle.set(new TransactTime(LocalDateTime.now()));
        return fixNewOrderSingle;
    }
}
