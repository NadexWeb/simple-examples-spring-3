package com.nadex.quickfixj.spring.boot.starter.examples.client.domain.from.fix;

import com.nadex.quickfixj.spring.boot.starter.examples.client.domain.MarketDataEntry;
import com.nadex.quickfixj.spring.boot.starter.examples.client.domain.MarketDataSnapshot;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.NoMDEntries;
import quickfix.fix50sp2.MarketDataSnapshotFullRefresh;

public class MarketDataSnapshotFactory {

    public static MarketDataSnapshot fromFix(MarketDataSnapshotFullRefresh marketDataSnapshotFullRefresh) throws FieldNotFound {
        MarketDataSnapshot marketDataSnapshot = new MarketDataSnapshot();
        marketDataSnapshot.setMsgType("W");
        if (marketDataSnapshotFullRefresh.isSetSymbol()) {
            marketDataSnapshot.setSymbol(String.valueOf(marketDataSnapshotFullRefresh.getSymbol().getValue()));
        }
        if (marketDataSnapshotFullRefresh.isSetMarketDepth()) {
            marketDataSnapshot.setMarketDepth(String.valueOf(marketDataSnapshotFullRefresh.getMarketDepth().getValue()));
        }
        if (marketDataSnapshotFullRefresh.isSetMDReqID()) {
            marketDataSnapshot.setMdReqId(String.valueOf(marketDataSnapshotFullRefresh.getMDReqID().getValue()));
        }
        if (marketDataSnapshotFullRefresh.isSetNoMDEntries() && marketDataSnapshotFullRefresh.getNoMDEntries().getValue() > 0) {
            int noMDEntriesCount = marketDataSnapshotFullRefresh.get(new NoMDEntries()).getValue();
            // noMdEntries is always 0 even if there are MD entries?
            for (int i = 1; i < noMDEntriesCount+1; i++) {
                Group mdEntryGroup = marketDataSnapshotFullRefresh.getGroup(i, new NoMDEntries().getTag());
                MarketDataEntry marketDataEntry = new MarketDataEntry();

                String entryType = String.valueOf(mdEntryGroup.getField(new MDEntryType()).getValue());
                String entryPx = String.valueOf(mdEntryGroup.getField(new MDEntryPx()).getValue());
                String entrySize = String.valueOf(mdEntryGroup.getField(new MDEntrySize()).getValue());

                marketDataEntry.setMdEntryType(entryType);
                marketDataEntry.setMdEntryPx(entryPx);
                marketDataEntry.setMdEntrySize(entrySize);
                if (entryType.equals("0")) {
                    marketDataSnapshot.setBid(marketDataEntry);
                }
                if (entryType.equals("1")) {
                    marketDataSnapshot.setOffer(marketDataEntry);
                }
            }
            marketDataSnapshot.setNoMmEntries(marketDataSnapshotFullRefresh.getNoMDEntries().getValue());
        }
        return marketDataSnapshot;
    }
}
