package com.nadex.quickfixj.spring.boot.starter.examples.client.domain;

import com.nadex.quickfixj.spring.boot.starter.examples.client.filter.FilterProperties;
import quickfix.field.Product;
import quickfix.field.SecurityListRequestType;
import quickfix.field.SecurityReqID;
import quickfix.field.SubscriptionRequestType;
import quickfix.fix50sp2.SecurityListRequest;

import java.util.List;
import java.util.UUID;

public class SecurityListRequestFactory {
    public static SecurityListRequest securityListRequest(FilterProperties filterProperties) {
        SecurityListRequest securityListRequest = new SecurityListRequest();
        securityListRequest.set(new SecurityReqID(UUID.randomUUID().toString()));
        List<String> products = filterProperties.getProducts();
        if (products.isEmpty()) {
            securityListRequest.set(new SecurityListRequestType(SecurityListRequestType.ALL_SECURITIES));
        } else {
            securityListRequest.set(new SecurityListRequestType(SecurityListRequestType.PRODUCT));
            securityListRequest.set(new Product(Integer.parseInt(products.getFirst())));
        }
        securityListRequest.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT));
        return securityListRequest;
    }
}
