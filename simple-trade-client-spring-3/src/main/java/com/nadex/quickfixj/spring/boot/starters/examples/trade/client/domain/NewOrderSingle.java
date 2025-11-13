package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewOrderSingle extends Message {
	private String symbol;
	private BigDecimal qty;
	private BigDecimal px;
	private String clientID;
	private String side; // String so verbose values can be used in UI
	private String ordType; // String so verbose values can be used in UI
	private String tif; // String so verbose values can be used in UI
}
