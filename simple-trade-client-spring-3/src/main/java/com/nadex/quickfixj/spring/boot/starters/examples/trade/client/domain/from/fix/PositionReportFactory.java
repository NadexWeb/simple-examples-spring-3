package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.from.fix;

import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.Party;
import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.PositionReport;
import quickfix.FieldNotFound;
import quickfix.field.MsgType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PositionReportFactory {
    public static PositionReport fromFix(quickfix.fix50sp2.PositionReport positionReport) throws FieldNotFound {
        PositionReport domainPositionReport = new PositionReport();
        domainPositionReport.setMsgType(MsgType.POSITION_REPORT);
        if (positionReport.isSetPosMaintRptID()) {
            domainPositionReport.setPosMaintRptID(positionReport.getPosMaintRptID().getValue());
        }
        if (positionReport.isSetPosReqID()) {
            domainPositionReport.setPosReqID(positionReport.getPosReqID().getValue());
        }
        if (positionReport.isSetPosReqType()) {
            domainPositionReport.setPosReqType(positionReport.getPosReqType().getValue());
        }
        if (positionReport.isSetClearingBusinessDate()) {
            domainPositionReport.setClearingBusinessDate(positionReport.getClearingBusinessDate().getValue());
        }
        if (positionReport.isSetSymbol()) {
            domainPositionReport.setSymbol(positionReport.getSymbol().getValue());
        }
        if (positionReport.isSetMaturityMonthYear()) {
            domainPositionReport.setMaturityMonthYear(positionReport.getMaturityMonthYear().getValue());
        }
        if (positionReport.isSetMaturityDay()) {
            domainPositionReport.setMaturityDay(positionReport.getMaturityDay().getValue());
        }
        if (positionReport.isSetSettlPrice()) {
            domainPositionReport.setSettlPrice(positionReport.getSettlPrice().getValue());
        }
        if (positionReport.isSetSettlPriceType()) {
            domainPositionReport.setSettlPriceType(positionReport.getSettlPriceType().getValue());
        }
        if (positionReport.isSetTotalNumPosReports()) {
            domainPositionReport.setTotalNumPosReports(positionReport.getTotalNumPosReports().getValue());
        }
        if (positionReport.isSetNoPartyIDs() && positionReport.getNoPartyIDs().getValue() > 0) {
            quickfix.fix50sp2.PositionReport.NoPartyIDs party = new quickfix.fix50sp2.PositionReport.NoPartyIDs();
            List<Party> parties = new ArrayList<>();
            for (int i=1; i < positionReport.getNoPartyIDs().getValue() + 1; i++) {
                positionReport.getGroup(i, party);
                Party domainParty = new Party(party.isSetPartyID() ? party.getPartyID().getValue() : null,
                                              party.isSetPartyIDSource() ? party.getPartyIDSource().getValue() : null,
                                              party.isSetPartyRole() ?  party.getPartyRole().getValue() : null);
                parties.add(domainParty);
            }
            domainPositionReport.setParties(parties);
        }
        if (positionReport.isSetNoUnderlyings() && positionReport.getNoUnderlyings().getValue() > 0) {
            quickfix.fix50sp2.PositionReport.NoUnderlyings fixUnderlying = new quickfix.fix50sp2.PositionReport.NoUnderlyings();
            List<PositionReport.Underlying> underlyings = new ArrayList<>();
            for (int i=1; i < positionReport.getNoUnderlyings().getValue() + 1; i++) {
                positionReport.getGroup(i, fixUnderlying);
                PositionReport.Underlying underlying =
                        new PositionReport.Underlying(
                           fixUnderlying.isSetUnderlyingSymbol() ? fixUnderlying.getUnderlyingSymbol().getValue(): null,
                           fixUnderlying.isSetUnderlyingSettlPrice() ? fixUnderlying.getUnderlyingSettlPrice().getValue(): null,
                           fixUnderlying.isSetUnderlyingSettlPriceType() ? fixUnderlying.getUnderlyingSettlPriceType().getValue(): null);
                underlyings.add(underlying);
            }
            domainPositionReport.setUnderlyings(underlyings);
        }
        if (positionReport.isSetNoPositions() && positionReport.getNoPositions().getValue() > 0) {
            quickfix.fix50sp2.PositionReport.NoPositions fixPosition = new quickfix.fix50sp2.PositionReport.NoPositions();
            List<PositionReport.PositionQty> positionQtys = new ArrayList<>();
            for (int i=1; i < positionReport.getNoPositions().getValue() + 1; i++) {
                positionReport.getGroup(i, fixPosition);
                PositionReport.PositionQty positionQty =
                   new PositionReport.PositionQty(
                        fixPosition.isSetPosType() ? fixPosition.getPosType().getValue(): null,
                        fixPosition.isSetLongQty() ? fixPosition.getLongQty().getValue(): null,
                        fixPosition.isSetShortQty() ? fixPosition.getShortQty().getValue(): null,
                        fixPosition.isSetPosQtyStatus() ? fixPosition.getPosQtyStatus().getValue(): null);
                positionQtys.add(positionQty);
            }
            domainPositionReport.setPositionQtys(positionQtys);
        }
        return domainPositionReport;
    }

    public static PositionReport cheekyLittleTestReport() {
        PositionReport positionReport = new PositionReport();

        positionReport.setMsgType(MsgType.POSITION_REPORT);
        positionReport.setPosMaintRptID("ABCD");
        positionReport.setPosReqID("12");
        positionReport.setPosReqType(99);
        positionReport.setClearingBusinessDate("2020-12-20");
        positionReport.setSymbol("Symbol");
        positionReport.setMaturityMonthYear("202510");
        positionReport.setMaturityDay("28"); //TODO field 205 is not in FIX protocol ?
        positionReport.setSettlPrice(new BigDecimal("100.0"));
        positionReport.setSettlPriceType(1);
        positionReport.setTotalNumPosReports(1);
        positionReport.setParties(new ArrayList<Party>() {{
            add(new Party("APartyID", 'D', 3));
        }});
        positionReport.setUnderlyings(new ArrayList<PositionReport.Underlying>() {{
            add(new PositionReport.Underlying("AnUnderlyingSymbol", new BigDecimal("100.0"), 1));
        }});
        positionReport.setPositionQtys(new ArrayList<PositionReport.PositionQty>() {{
            add(new PositionReport.PositionQty("TQ", new BigDecimal(10), new BigDecimal(0), 1));
        }});
        return positionReport;
    }
}
