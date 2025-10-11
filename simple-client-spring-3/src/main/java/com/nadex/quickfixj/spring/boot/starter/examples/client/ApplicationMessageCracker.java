/*
 * Copyright 2017-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nadex.quickfixj.spring.boot.starter.examples.client;

import com.nadex.quickfixj.spring.boot.starter.examples.client.domain.InstrumentFactory;
import com.nadex.quickfixj.spring.boot.starter.examples.client.domain.Instrument;
import com.nadex.quickfixj.spring.boot.starter.examples.client.filter.FilterProperties;
import lombok.extern.slf4j.Slf4j;
import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.Session;
import quickfix.SessionNotFound;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.*;
import quickfix.fix50sp2.*;
import quickfix.fix50sp2.SecurityStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * ApplicationMessageCracker extends the QuickFIX/J MessageCracker to provide an implementation of callbacks
 * for the messages of interest
 */
@Slf4j
public class ApplicationMessageCracker extends MessageCracker {

    private final Set<String> underlyingSymbols = new HashSet<>();

    private final Set<String> products = new HashSet<>();

    private final Set<String> securitySubTypes = new HashSet<>();

    private final Set<Pattern> symbolRegularExpressionPatterns = new HashSet<>();

    private final Set<String> periods = new HashSet<>();

    public static final String EMPTY_STRING = "";

    public ApplicationMessageCracker(FilterProperties filterProperties) {
        if (null != filterProperties) {
            this.underlyingSymbols.addAll(filterProperties.getUnderlyingSymbols());
            this.underlyingSymbols.forEach(s -> log.info("Configured Filter Underlying Symbol {}", s));
            this.products.addAll(filterProperties.getProducts());
            this.products.forEach(s -> log.info("Configured Filter Product {}", s));
            this.securitySubTypes.addAll(filterProperties.getSecuritySubTypes());
            this.securitySubTypes.forEach(s -> log.info("Configured Filter SecuritySubType {}", s));
            this.periods.addAll(filterProperties.getPeriods());
            this.periods.forEach(s -> log.info("Configured Filter Period {}", s));
            filterProperties.getSymbolRegularExpressions().forEach(
                    s -> {
                        this.symbolRegularExpressionPatterns.add(Pattern.compile(s));
                        log.info("Configured Filter SymbolRegularExpression {}", s);
                    });
        }
    }

    @Override
    public void onMessage(SecurityList securityList, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        int securityRequestResult = securityList.getSecurityRequestResult().getValue();
        if (securityRequestResult == SecurityRequestResult.VALID_REQUEST) {
            int noRelatedSym = securityList.getNoRelatedSym().getValue();
            log.info("Valid SecurityList received, SecurityListRequest Result: {}, No Related Sym: {}", securityRequestResult, noRelatedSym);
            List<Instrument> instruments = processSecurityList(securityList, noRelatedSym);
            if (!instruments.isEmpty()) {
                log.debug("Processing instruments {}", instruments.size());
                instruments.forEach(instrument -> {
                    try {
                        log.debug("Received instrument {}", instrument.getSymbol());
                        String symbol = instrument.getSymbol();
                        // Security Status messages are sent unsolicited so the request is commented out
                        // Session.sendToTarget(SecurityStatusRequestFactory.securityStatusRequest(symbol), sessionID);
                        Session.sendToTarget(MarketDataRequestFactory.createMarketDataRequest(instrument), sessionID);
                        log.info("Security Status requested for {}", symbol);
                    } catch (SessionNotFound e) {
                        String message = String.format("Unexpected SessionNotFound exception corresponding to received message, SessionID: %s", sessionID.toString());
                        log.error(message);
                    }
                });
            } else {
                log.info("No instruments found for instrument provider matching configured criteria.");
            }
        } else {
            log.error("SecurityListRequest received, Result value {} is not VALID_REQUEST.", securityRequestResult);
        }
    }

    @Override
    public void onMessage(TradingSessionStatus tradingSessionStatus, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        int tradingSessionStatusValue = tradingSessionStatus.getTradSesStatus().getValue();
        log.info("received TradingSessionStatus: {}", tradingSessionStatusValue);
        if (TradSesStatus.OPEN != tradingSessionStatusValue) {
            log.info("Session Status for {} is not OPEN, TradingSessionStatus: {}", tradingSessionStatus.getSymbol().getValue(), tradingSessionStatusValue);
        }
    }

    @Override
    public void onMessage(SecurityStatus securityStatus, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        String symbol = securityStatus.getSymbol().getValue();
        int securityTradingStatus = securityStatus.getSecurityTradingStatus().getValue();
        log.debug("Received SecurityStatus, Symbol:{} Security Trading Status:{}", symbol, securityTradingStatus);
    }

    @Override
    public void onMessage(MarketDataSnapshotFullRefresh marketDataSnapshotFullRefresh, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        log.info("Received Market Data Snapshot Full Refresh, Symbol:{}, number of MDEntries {}",
                marketDataSnapshotFullRefresh.getSymbol().getValue(), marketDataSnapshotFullRefresh.getNoMDEntries().getValue());
    }

    @Override
    public void onMessage(MarketDataIncrementalRefresh marketDataIncrementalRefresh, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        log.info("Received Market Data Snapshot Incremental Refresh, Symbol:{}, number of MDEntries {}",
                marketDataIncrementalRefresh.getSymbol().getValue(), marketDataIncrementalRefresh.getNoMDEntries().getValue());
    }

    /**
     * Processing the received SecurityList, retaining Instruments that pass the configured filters
     * @param securityList the list of Securities
     * @param noRelatedSym the number of Securities in the received list
     * @return List of Instruments
     * @throws FieldNotFound if an expected field is not found
     */
    private List<Instrument> processSecurityList(SecurityList securityList, int noRelatedSym) throws FieldNotFound {
        log.debug("noRelatedSym {}", noRelatedSym);
        List<Instrument> instruments = new ArrayList<>();
		SecurityList.NoRelatedSym noRelatedSymGroup = new SecurityList.NoRelatedSym();
		// notice that the QuickFIX/J group iteration is indexed to 1 not 0
		int iterationBoundary = noRelatedSym + 1; // 1 indexed so add 1
        for (int i = 1; i < iterationBoundary; i++) {
            securityList.getGroup(i, noRelatedSymGroup);
            Optional<Instrument> optionalInstrument = createInstrumentIfFiltersPassed(noRelatedSymGroup);
            optionalInstrument.ifPresent(instruments::add);
        }
        return instruments;
    }

    /**
     * Returns Optional<String> of the Instrument Period : I, D, W, M, O
     * @param noRelatedSymGroup the related symbol group to process
     * @return Optional af Instrument Period
     * @throws FieldNotFound if an expected field not found
     */
    private static Optional<String> getPeriod(SecurityList.NoRelatedSym noRelatedSymGroup) throws FieldNotFound {
        SecurityList.NoRelatedSym.NoInstrAttrib noInstrAttribGroup = new SecurityList.NoRelatedSym.NoInstrAttrib();
        NoInstrAttrib noInstrAttrib = noRelatedSymGroup.getNoInstrAttrib();
        int instrAttribIterations = noInstrAttrib.getValue() + 1;
        for (int i = 1; i < instrAttribIterations; i++) {
            noRelatedSymGroup.getGroup(i, noInstrAttribGroup);
            if (noInstrAttribGroup.isSetInstrAttribType()) {
                if(noInstrAttribGroup.getInstrAttribType().getValue() == InstrAttribType.PERIOD) {
                    return Optional.of(noInstrAttribGroup.getInstrAttribValue().getValue());
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Creates an Instrument based on that received in SecurityList if the filters are passed
     * @param noRelatedSymGroup an instance of the related symbols repeating group
     * @return Instrument An Instrument if the filters are satisfied
     * @throws FieldNotFound if an expected field not found
     */
    private Optional<Instrument> createInstrumentIfFiltersPassed(SecurityList.NoRelatedSym noRelatedSymGroup) throws FieldNotFound {
        String symbol = noRelatedSymGroup.getSymbol().getValue();
        final String product = Integer.toString(noRelatedSymGroup.getProduct().getValue());
        final String securitySubType = noRelatedSymGroup.getSecuritySubType().getValue();
        // if underlyingSymbols are configured, there must be a match in this underlyings group
        Set<String> matchedUnderlyingSymbols = new HashSet<>();
        if (!this.underlyingSymbols.isEmpty()) {
            // underlyingSymbols filter has been configured
            matchedUnderlyingSymbols.addAll(getMatchedUnderlyingSymbols(this.underlyingSymbols, noRelatedSymGroup));
            if (matchedUnderlyingSymbols.isEmpty()) {
                return Optional.empty();
            }
        }
        // if products are configured, there must be a match
        if (!this.products.isEmpty()) {
            // products filter has been configured
            if(!this.products.contains(product)) {
                return Optional.empty();
            }
        }
        // if securitySubTypes are configured, there must be a match
        if (!this.securitySubTypes.isEmpty()) {
            // securitySubTypes filter has been configured
            if (!this.securitySubTypes.contains(securitySubType)) {
                return Optional.empty();
            }
        }
        // if symbolRegularExpressionPatterns are configured, there must be a match
        if (!this.symbolRegularExpressionPatterns.isEmpty()) {
            if (this.symbolRegularExpressionPatterns.
                    stream().noneMatch(pattern -> pattern.matcher(symbol).find())) {
                return Optional.empty();
            }
        }
        // if periodCodes are configured, there must be a match
        if (!this.periods.isEmpty()) {
            Optional<String> period = getPeriod(noRelatedSymGroup);
            if (period.isEmpty() || !this.periods.contains(period.get()) ) {
                return Optional.empty();
            }
        }

        // filters have all been passed create and return Instrument
        final BigDecimal minPrice = noRelatedSymGroup.isSetField(FloorPrice.FIELD) ? noRelatedSymGroup.getFloorPrice().getValue() : new BigDecimal(0);
        final BigDecimal maxPrice = noRelatedSymGroup.isSetField(CapPrice.FIELD) ? noRelatedSymGroup.getCapPrice().getValue() : new BigDecimal(0);
        final double minPriceIncrement = noRelatedSymGroup.isSetField(UnitOfMeasure.FIELD) ? Double.parseDouble(noRelatedSymGroup.getUnitOfMeasure().getValue()): 0d;
        final String securityDescription = noRelatedSymGroup.isSetField(SecurityDesc.FIELD) ? noRelatedSymGroup.getSecurityDesc().getValue(): EMPTY_STRING;
        matchedUnderlyingSymbols.forEach(underlyingSymbol -> log.info("Matched Underlying : Security[Underlying Symbol: {}, Symbol: {}, Product: {}, Security Sub Type {}, Desc: {}, MinPrice: {}, MaxPrice: {}, MinPriceIncrement: {}]",
                underlyingSymbol,
                symbol,
                product,
                securitySubType,
                securityDescription,
                minPrice,
                maxPrice,
                minPriceIncrement));

        return Optional.of(InstrumentFactory.newInstrument(symbol, minPrice, maxPrice, product, securitySubType, minPriceIncrement));
    }

    /**
     * Returns the underlying symbols that match the configured underlyings filter or <code>null</code>
     * @param noRelatedSymGroup the related symbol group to process
     * @return the matched underlying symbols
     * @throws FieldNotFound Field Not Found
     */
    private static Set<String> getMatchedUnderlyingSymbols(Set<String> underlyingSymbolsToMatch, SecurityList.NoRelatedSym noRelatedSymGroup) throws FieldNotFound {
        Set<String> matchedUnderlyingSymbols = new HashSet<>();
        NoUnderlyings noUnderlyings = noRelatedSymGroup.getNoUnderlyings();
        int underlyingIterations = noUnderlyings.getValue() + 1;
        SecurityList.NoRelatedSym.NoUnderlyings noUnderlyingsGroup = new SecurityList.NoRelatedSym.NoUnderlyings();
        for (int i = 1; i < underlyingIterations; i++) {
            noRelatedSymGroup.getGroup(i, noUnderlyingsGroup);
            if (noUnderlyingsGroup.isSetUnderlyingSymbol()) {
                String underlyingSymbol = noUnderlyingsGroup.getUnderlyingSymbol().getValue();
                if (underlyingSymbolsToMatch.contains(underlyingSymbol)) {
                    matchedUnderlyingSymbols.add(underlyingSymbol);
                }
            }
        }
        return matchedUnderlyingSymbols;
    }
}

