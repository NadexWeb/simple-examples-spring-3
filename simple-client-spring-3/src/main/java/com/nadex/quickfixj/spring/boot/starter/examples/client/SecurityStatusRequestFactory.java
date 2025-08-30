package com.nadex.quickfixj.spring.boot.starter.examples.client;

import quickfix.Message;
import quickfix.field.SecurityStatusReqID;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.fix50sp2.SecurityStatusRequest;

import java.util.UUID;

public class SecurityStatusRequestFactory {
    public static Message securityStatusRequest(String symbol) {
        SecurityStatusRequest statusRequest = new SecurityStatusRequest();
        statusRequest.set(new SecurityStatusReqID(UUID.randomUUID().toString()));
        statusRequest.set(new Symbol(symbol));
        statusRequest.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_UPDATES));
        return statusRequest;
    }
}
