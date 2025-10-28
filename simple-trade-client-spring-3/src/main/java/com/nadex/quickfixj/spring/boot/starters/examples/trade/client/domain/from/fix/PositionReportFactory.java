package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.from.fix;

import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.Party;
import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.PositionReport;
import quickfix.FieldNotFound;

import java.util.ArrayList;

public class PositionReportFactory {
    public static PositionReport fromFix(quickfix.fix50sp2.PositionReport positionReport) throws FieldNotFound {
        PositionReport domainPositionReport = new PositionReport();
        if (positionReport.isSetPosMaintRptID()) {
            domainPositionReport.setPosMaintRptID(positionReport.getPosMaintRptID().getValue());
        }
        if (positionReport.isSetPosReqID()) {
            domainPositionReport.setPosReqID(positionReport.getPosReqID().getValue());
        }
        if (positionReport.isSetPosReqType()) {
            domainPositionReport.setPosReqType(positionReport.getPosReqType().getValue());
        }
        return domainPositionReport;
    }

    public static PositionReport cheekyLittleTestReport() {
        PositionReport positionReport = new PositionReport();
        positionReport.setPosMaintRptID("ABCD");
        positionReport.setPosReqID("12");
        positionReport.setPosReqType(99);
        positionReport.setClearingBusinessDate("2020-12-20");
        positionReport.setSymbol("Symbol");
        positionReport.setMaturityMonthYear("202510");
        positionReport.setMaturityDay("28"); //TODO field 205 is not in FIX protocol ?
        positionReport.setSettlPrice("100.0");
        positionReport.setSettlPriceType(1);
        positionReport.setTotalNumPosReports(1);
        positionReport.setParties(new ArrayList<Party>() {{
            add(new Party("APartyID", 'D', 3));
        }});
        positionReport.setUnderlyings(new ArrayList<PositionReport.Underlying>() {{
            add(new PositionReport.Underlying("AnUnderlyingSymbol", "100.0", 1));
        }});
        positionReport.setPositionQtys(new ArrayList<PositionReport.PositionQty>() {{
            add(new PositionReport.PositionQty("TQ", 10, 0, 1));
        }});
        return positionReport;
    }
}
