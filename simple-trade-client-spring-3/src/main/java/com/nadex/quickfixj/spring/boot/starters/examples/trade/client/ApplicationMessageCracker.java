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
package com.nadex.quickfixj.spring.boot.starters.examples.trade.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Component;
import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.fix50sp2.MessageCracker;

/**
 * ApplicationMessageCracker extends the QuickFIX/J MessageCracker to provide an implementation of callbacks
 * for the messages of interest
 */
@Slf4j
@Component
public class ApplicationMessageCracker extends MessageCracker {

    public static final String PATH = "/topic/messages";
    private final MessageSendingOperations<String> messageSendingOperations;

    public ApplicationMessageCracker(MessageSendingOperations<String> messageSendingOperations) {
        this.messageSendingOperations = messageSendingOperations;
    }

    @Override
    public void onMessage(quickfix.fix50sp2.ExecutionReport executionReport, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        log.info("ExecutionReport received: {}", executionReport);
        this.messageSendingOperations.convertAndSend(PATH, ExecutionReportFactory.executionReportFromFixExecutionReport(executionReport));
    }

    @Override
    public void onMessage(quickfix.fix50sp2.BusinessMessageReject businessMessageReject, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        log.info("BusinessMessageReject received: {}", businessMessageReject);
        this.messageSendingOperations.convertAndSend(PATH, BusinessMessageRejectFactory.businessMessageRejectFromFix(businessMessageReject));
    }

}

