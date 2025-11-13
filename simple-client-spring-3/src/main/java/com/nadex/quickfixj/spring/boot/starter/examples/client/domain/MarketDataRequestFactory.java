package com.nadex.quickfixj.spring.boot.starter.examples.client.domain;

import quickfix.field.*;
import quickfix.fix50sp2.MarketDataRequest;

import java.util.*;

public class MarketDataRequestFactory {

    private static final char[] mdEntryTypesArray = {MDEntryType.BID, MDEntryType.OFFER, MDEntryType.TRADE};


    public static MarketDataRequest createMarketDataRequest(Instrument instrument) {
        MarketDataRequest marketDataRequest = new MarketDataRequest();
        marketDataRequest.set(new MDReqID(UUID.randomUUID().toString()));
        //request snapshot plus updates
        marketDataRequest.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_UPDATES));
        // Market Depth could have a type safe enumeration
        marketDataRequest.set(new MarketDepth(0));
        marketDataRequest.set(new AggregatedBook(AggregatedBook.BOOK_ENTRIES_TO_BE_AGGREGATED));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(instrument.getSymbol()));
        marketDataRequest.addGroup(noRelatedSym);
        MarketDataRequest.NoMDEntryTypes noMDEntryTypeGroup = new MarketDataRequest.NoMDEntryTypes();
        for(char mdEntryType : mdEntryTypesArray) {
            noMDEntryTypeGroup.set(new MDEntryType(mdEntryType));
            marketDataRequest.addGroup(noMDEntryTypeGroup);
        }
        return marketDataRequest;
    }
}
