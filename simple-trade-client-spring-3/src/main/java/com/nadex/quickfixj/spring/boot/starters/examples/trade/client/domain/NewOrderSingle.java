package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class NewOrderSingle extends Message {
	private String symbol;
	private String qty;
	private String px;
	private String clientID;
	private String side;
	private String ordType;
	private String tif;
}
