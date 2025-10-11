package com.nadex.quickfixj.spring.boot.starters.examples.trade.client;

import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.ExecutionReport;
import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.NewOrderSingle;
import quickfix.FieldNotFound;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class ExecutionReportFactory {
    public static ExecutionReport executionReportFromNewOrderSingle(NewOrderSingle newOrderSingle) {
        ExecutionReport executionReport = new ExecutionReport();
        executionReport.setSymbol(newOrderSingle.getSymbol());
        executionReport.setOrderID(UUID.randomUUID().toString());
        executionReport.setExecID(UUID.randomUUID().toString());
        executionReport.setSide(newOrderSingle.getSide());
        executionReport.setAvgPx(newOrderSingle.getPx());
        executionReport.setPrice(newOrderSingle.getPx());
        executionReport.setClientOrderID(UUID.randomUUID().toString());
        executionReport.setCumQty(newOrderSingle.getQty());
        executionReport.setOrderQty(newOrderSingle.getQty());
        executionReport.setLastQty(newOrderSingle.getQty());
        executionReport.setLeavesQty("0.0");
        executionReport.setTransactTime(Instant.now().toString() );
        executionReport.setOrdStatus("2");
        executionReport.setTradeDate(LocalDate.now().toString());
        return executionReport;
    }

    public static com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.ExecutionReport
        executionReportFromFixExecutionReport(quickfix.fix50sp2.ExecutionReport executionReport) throws FieldNotFound {
        com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.ExecutionReport domainExecutionReport = new ExecutionReport();
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
            domainExecutionReport.setSide(executionReport.getSide().toString());
        }
        if (executionReport.isSetAvgPx()) {
            domainExecutionReport.setAvgPx(executionReport.getAvgPx().toString());
        }
        if (executionReport.isSetPrice()) {
            domainExecutionReport.setPrice(executionReport.getPrice().toString());
        }
        if (executionReport.isSetClOrdID()) {
            domainExecutionReport.setClientOrderID(executionReport.getClOrdID().getValue());
        }
        if (executionReport.isSetCumQty()) {
            domainExecutionReport.setCumQty(executionReport.getCumQty().toString());
        }
        if (executionReport.isSetOrderQty()) {
            domainExecutionReport.setOrderQty(executionReport.getOrderQty().toString());
        }
        if (executionReport.isSetLastQty()) {
            domainExecutionReport.setLastQty(executionReport.getLastQty().toString());
        }
        if (executionReport.isSetLeavesQty()) {
            domainExecutionReport.setLeavesQty(executionReport.getLeavesQty().toString());
        }
        if (executionReport.isSetTransactTime()) {
            domainExecutionReport.setTransactTime(executionReport.getTransactTime().toString());
        }
        if (executionReport.isSetOrdStatus()) {
            domainExecutionReport.setOrdStatus(executionReport.getOrdStatus().toString());
        }
        if (executionReport.isSetTradeDate()) {
            domainExecutionReport.setTradeDate(executionReport.getTradeDate().toString());
        }
        return domainExecutionReport;
    }
}
