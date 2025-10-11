package com.nadex.quickfixj.spring.boot.starters.examples.trade.client;

import io.allune.quickfixj.spring.boot.starter.EnableQuickFixJClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import quickfix.Application;
import quickfix.fix50sp2.MessageCracker;

@SpringBootApplication

@Slf4j
@EnableQuickFixJClient
@EnableWebSocketMessageBroker
public class TradeClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeClientApplication.class, args);
	}

	@Bean
	public Application clientApplication(ApplicationMessageCracker messageCracker,
										 NewOrderSingleController newOrderSingleController) {
		return new ApplicationAdapter(messageCracker, newOrderSingleController);
	}
}
