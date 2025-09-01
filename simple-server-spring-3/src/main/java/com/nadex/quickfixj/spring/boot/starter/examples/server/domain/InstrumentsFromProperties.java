package com.nadex.quickfixj.spring.boot.starter.examples.server.domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class InstrumentsFromProperties {
    @Getter
    @Setter
    private List<Instrument> instruments;
}
