package com.nadex.quickfixj.spring.boot.starter.examples.client.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.filter")
public class FilterProperties {

    @Getter
    @Setter
    private List<String> underlyingSymbols;

    @Getter
    @Setter
    private List<String> products;

    @Getter
    @Setter
    private List<String> securitySubTypes;

    @Getter
    @Setter
    private List<String> symbolRegularExpressions;

    @Getter
    @Setter
    private List<String> periods;

}
