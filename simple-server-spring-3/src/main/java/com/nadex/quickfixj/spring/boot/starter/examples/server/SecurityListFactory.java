package com.nadex.quickfixj.spring.boot.starter.examples.server;

import com.nadex.quickfixj.spring.boot.starter.examples.server.domain.Instrument;
import lombok.extern.slf4j.Slf4j;
import quickfix.fix50sp2.SecurityList;
import quickfix.field.SecurityReqID;
import quickfix.field.SecurityRequestResult;
import quickfix.field.Symbol;
import quickfix.field.MinPriceIncrement;
import quickfix.field.FloorPrice;
import quickfix.field.CapPrice;
import quickfix.field.Currency;
import quickfix.field.Product;
import quickfix.field.SecuritySubType;
import quickfix.field.UnderlyingSymbol;
import quickfix.field.InstrAttribType;
import quickfix.field.InstrAttribValue;

import java.util.List;

@Slf4j
public class SecurityListFactory {

    public static final int INSTR_ATTRIB_TYPE_PERIOD_CODE = 511;

    public static SecurityList securityList(List<Instrument> instruments, SecurityReqID securityReqID) {
        log.debug("instruments size {}", instruments.size());
        SecurityList securityList = new SecurityList();
        securityList.set(securityReqID);
        securityList.set(new SecurityRequestResult(SecurityRequestResult.VALID_REQUEST));
        instruments.forEach(instrument -> addInstrument(instrument, securityList));
        return securityList;
    }

    private static void addInstrument(Instrument instrument, SecurityList securityList) {
        log.debug("adding {}", instrument.getSymbol());
        SecurityList.NoRelatedSym noRelatedSymGroup = new SecurityList.NoRelatedSym();
        noRelatedSymGroup.set(new Symbol(instrument.getSymbol()));
        noRelatedSymGroup.set(new Product(instrument.getProduct()));
        noRelatedSymGroup.set(new SecuritySubType(instrument.getSecuritySubType()));
        noRelatedSymGroup.set(new MinPriceIncrement(instrument.getMinPriceIncrement()));
        noRelatedSymGroup.set(new CapPrice(instrument.getMaxPrice()));
        noRelatedSymGroup.set(new FloorPrice(instrument.getMinPrice()));

        SecurityList.NoRelatedSym.NoInstrAttrib noInstrAttribGroup = new SecurityList.NoRelatedSym.NoInstrAttrib();
        noInstrAttribGroup.set(new InstrAttribType(INSTR_ATTRIB_TYPE_PERIOD_CODE));
        noInstrAttribGroup.set(new InstrAttribValue(instrument.getPeriod()));
        noRelatedSymGroup.addGroup(noInstrAttribGroup);

        SecurityList.NoRelatedSym.NoUnderlyings noUnderlyingsGroup = new SecurityList.NoRelatedSym.NoUnderlyings();
        noUnderlyingsGroup.set(new UnderlyingSymbol(instrument.getUnderlyingSymbol()));
        noRelatedSymGroup.addGroup(noUnderlyingsGroup);

        noRelatedSymGroup.set(new Currency(instrument.getCurrency()));

        securityList.addGroup(noRelatedSymGroup);
    }

}
