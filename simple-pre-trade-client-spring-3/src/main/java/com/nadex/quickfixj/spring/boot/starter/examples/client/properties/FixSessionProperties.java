package com.nadex.quickfixj.spring.boot.starter.examples.client.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.session")
public class FixSessionProperties {
    @Getter
    @Setter
    private String apikey;

    @Getter
    @Setter
    private String secret;
}
