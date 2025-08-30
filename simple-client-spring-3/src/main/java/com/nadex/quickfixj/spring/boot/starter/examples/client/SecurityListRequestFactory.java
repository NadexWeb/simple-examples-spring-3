package com.nadex.quickfixj.spring.boot.starter.examples.client;

import quickfix.field.SecurityListRequestType;
import quickfix.field.SecurityReqID;
import quickfix.field.SubscriptionRequestType;
import quickfix.fix50sp2.SecurityListRequest;

import java.util.UUID;

public class SecurityListRequestFactory {
    public static SecurityListRequest securityListRequest() {
        SecurityListRequest securityListRequest = new SecurityListRequest();
        securityListRequest.set(new SecurityReqID(UUID.randomUUID().toString()));
        securityListRequest.set(new SecurityListRequestType(SecurityListRequestType.ALL_SECURITIES));
        securityListRequest.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT));
        return securityListRequest;
    }
}
