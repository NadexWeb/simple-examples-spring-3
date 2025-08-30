package com.nadex.quickfixj.spring.boot.starter.examples.client.domain;

import java.math.BigDecimal;

public class InstrumentFactory {
    public static Instrument newInstrument(String symbol, BigDecimal minPrice, BigDecimal maxPrice, String product, String securitySubType, double minPriceIncrement) {
        Instrument instrument = new Instrument();
        instrument.setSymbol(symbol);
        instrument.setProduct(product);
        instrument.setSecuritySubType(securitySubType);
        instrument.setMinPrice(minPrice);
        instrument.setMaxPrice(maxPrice);
        instrument.setMinPriceIncrement(minPriceIncrement);
        return instrument;
    }
}
