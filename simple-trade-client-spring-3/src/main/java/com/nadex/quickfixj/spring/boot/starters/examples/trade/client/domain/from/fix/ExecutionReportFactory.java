package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.from.fix;

import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.ExecutionReport;
import quickfix.FieldNotFound;
import quickfix.field.MsgType;

public class ExecutionReportFactory {
    /**
     * Returns a Domain value object from the FIX Execution Report
     * @param executionReport FIX Execution Report
     * @return Domain Execution Report
     * @throws FieldNotFound QuickFIX/J Exception
     */
    public static com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.ExecutionReport
    fromFix(quickfix.fix50sp2.ExecutionReport executionReport) throws FieldNotFound {
        com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.ExecutionReport domainExecutionReport = new ExecutionReport();
        domainExecutionReport.setMsgType(executionReport.getHeader().getString(MsgType.FIELD));
        if (executionReport.isSetSymbol()) {
            domainExecutionReport.setSymbol(executionReport.getSymbol().getValue());
        }
        if (executionReport.isSetOrderID()) {
            domainExecutionReport.setOrderID(executionReport.getOrderID().getValue());
        }
        if (executionReport.isSetExecID()) {
            domainExecutionReport.setExecID(executionReport.getExecID().getValue());
        }
        if (executionReport.isSetSide()) {
            domainExecutionReport.setSide(String.valueOf(executionReport.getSide().getValue()));
        }
        if (executionReport.isSetAvgPx()) {
            domainExecutionReport.setAvgPx(executionReport.getAvgPx().getValue().toString());
        }
        if (executionReport.isSetPrice()) {
            domainExecutionReport.setPrice(executionReport.getPrice().getValue().toString());
        }
        if (executionReport.isSetClOrdID()) {
            domainExecutionReport.setClientOrderID(executionReport.getClOrdID().getValue());
        }
        if (executionReport.isSetCumQty()) {
            domainExecutionReport.setCumQty(executionReport.getCumQty().getValue().toString());
        }
        if (executionReport.isSetOrderQty()) {
            domainExecutionReport.setOrderQty(executionReport.getOrderQty().getValue().toString());
        }
        if (executionReport.isSetLastQty()) {
            domainExecutionReport.setLastQty(executionReport.getLastQty().getValue().toString());
        }
        if (executionReport.isSetLeavesQty()) {
            domainExecutionReport.setLeavesQty(executionReport.getLeavesQty().getValue().toString());
        }
        if (executionReport.isSetTransactTime()) {
            domainExecutionReport.setTransactTime(executionReport.getTransactTime().getValue().toString());
        }
        if (executionReport.isSetOrdStatus()) {
            domainExecutionReport.setOrdStatus(String.valueOf(executionReport.getOrdStatus().getValue()));
        }
        if (executionReport.isSetTradeDate()) {
            domainExecutionReport.setTradeDate(executionReport.getTradeDate().getValue().toString());
        }
        if (executionReport.isSetText()) {
            domainExecutionReport.setText(executionReport.getText().getValue().toString());
        }
        return domainExecutionReport;
    }
}
