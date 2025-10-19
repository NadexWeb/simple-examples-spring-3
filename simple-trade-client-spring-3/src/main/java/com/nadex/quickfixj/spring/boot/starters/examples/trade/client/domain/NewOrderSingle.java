package com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain;

import lombok.Data;

@Data
public class NewOrderSingle extends Message {
	public String symbol;
	public String qty;
	public String px;
	public String clientID;
	public String side;
	public String ordType;
	public String tif;
}
