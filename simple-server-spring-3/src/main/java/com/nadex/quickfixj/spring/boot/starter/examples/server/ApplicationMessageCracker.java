package com.nadex.quickfixj.spring.boot.starter.examples.server;

import com.nadex.quickfixj.spring.boot.starter.examples.server.domain.InstrumentsFromProperties;
import lombok.extern.slf4j.Slf4j;
import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.Session;
import quickfix.SessionNotFound;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.*;
import quickfix.fix50sp2.MessageCracker;
import quickfix.fix50sp2.SecurityList;
import quickfix.fix50sp2.BusinessMessageReject;
import quickfix.fix50sp2.SecurityList;
import quickfix.fix50sp2.SecurityListRequest;

@Slf4j
public class ApplicationMessageCracker extends MessageCracker {

//    private final ScheduledExecutorService executor =
//            Executors.newSingleThreadScheduledExecutor();
    private final InstrumentsFromProperties instrumentsFromProperties;

    public ApplicationMessageCracker(InstrumentsFromProperties instrumentsFromProperties) {
        this.instrumentsFromProperties = instrumentsFromProperties;
        log.debug("instruments size {}", instrumentsFromProperties.getInstruments().size());
    }

    public void onMessage(SecurityListRequest securityListRequest, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        SecurityReqID securityReqID = securityListRequest.getSecurityReqID();
        SecurityList securityList = SecurityListFactory.securityList(instrumentsFromProperties.getInstruments(), securityReqID);
//        executor.execute(() -> {
            try {
                Session.sendToTarget(securityList, sessionID);
            } catch (SessionNotFound e) {
                log.error("Session Not Found", e);
            }
//        });
    }

    public void onMessage(BusinessMessageReject message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        log.error("Received Business Message Reject", message);
    }

}
