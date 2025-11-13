package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Party {
    private String partyID;
    private Character partyIDSource;
    private Integer partyRole;
}
