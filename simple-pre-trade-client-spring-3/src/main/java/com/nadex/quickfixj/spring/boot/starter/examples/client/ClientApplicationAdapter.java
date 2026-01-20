/*
 * Copyright 2017-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nadex.quickfixj.spring.boot.starter.examples.client;

import com.nadex.quickfixj.spring.boot.starter.examples.client.domain.SecurityListRequestFactory;
import com.nadex.quickfixj.spring.boot.starter.examples.client.properties.FilterProperties;
import com.nadex.quickfixj.spring.boot.starter.examples.client.properties.FixSessionProperties;
import com.nadex.spring.boot.starters.examples.common.LogonMessageDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix50sp2.MessageCracker;

public class ClientApplicationAdapter implements Application {

	private static final Logger log = LoggerFactory.getLogger(ClientApplicationAdapter.class);

	private final MessageCracker messageCracker;
	private final FilterProperties filterProperties;
	private final FixSessionProperties fixSessionProperties;

	public ClientApplicationAdapter(MessageCracker messageCracker, FilterProperties filterProperties, FixSessionProperties fixSessionProperties) {
		this.messageCracker = messageCracker;
		this.filterProperties = filterProperties;
		this.fixSessionProperties = fixSessionProperties;
	}

	@Override
	public void fromAdmin(Message message, SessionID sessionId) {
		log.info("fromAdmin: Message={}, SessionId={}", message, sessionId);

	}

	@Override
	public void fromApp(Message message, SessionID sessionId) {
		log.debug("fromApp: Message={}, SessionId={}", message, sessionId);
		try {
			messageCracker.crack(message, sessionId);
		} catch (UnsupportedMessageType | FieldNotFound | IncorrectTagValue e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void onCreate(SessionID sessionId) {
		log.info("onCreate: SessionId={}", sessionId);
	}

	@Override
	public void onLogon(SessionID sessionId) {
		log.info("onLogon: SessionId={}", sessionId);
		try {
			// Request Security List
			Session.sendToTarget(SecurityListRequestFactory.securityListRequest(this.filterProperties), sessionId);
		} catch (SessionNotFound e) {
			String message = String.format("SessionNotFound exception for Session that just logged on: %s", sessionId);
			log.error(message);
		}
	}

	@Override
	public void onLogout(SessionID sessionId) {
		log.info("onLogout: SessionId={}", sessionId);
	}

	@Override
	public void toAdmin(Message message, SessionID sessionId) {
		log.info("toAdmin: received Message={}, SessionId={}", message, sessionId);
		try {
			MsgType msgType = new MsgType();
			if (message.getHeader().getField(msgType).getValue().equals("A")) {
				LogonMessageDecorator.decorate(message,
						fixSessionProperties.getApikey(),
						fixSessionProperties.getSecret());
				log.info("toAdmin: Logon message updated, Message={}, SessionId={}", message, sessionId);
			}
		} catch (Exception e) {
			log.error("toAdmin: Abnormal termination due to {}", e.getMessage(), e);
			System.exit(1);
		}
	}

	@Override
	public void toApp(Message message, SessionID sessionId) {
		log.info("toApp: Message={}, SessionId={}", message, sessionId);
	}
}
