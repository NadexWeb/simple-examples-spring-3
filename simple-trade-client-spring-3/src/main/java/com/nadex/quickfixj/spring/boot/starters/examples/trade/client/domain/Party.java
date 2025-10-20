package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class Party {
    private String partyID;
    private String partyRole;
}
