package com.nadex.quickfixj.spring.boot.starters.examples.trade.client;

import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.NewOrderSingle;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import quickfix.Session;
import quickfix.SessionID;

@Controller
@Slf4j
@Component
public class NewOrderSingleController {

    @Getter @Setter private SessionID sessionID;

    public NewOrderSingleController() {
    }

    @MessageMapping("/new-order-single")
    public void setNewOrderSingle(NewOrderSingle newOrderSingle) throws Exception {
        log.info("New Order Single Received: {}", newOrderSingle);

        if (null != this.sessionID) {
            quickfix.fix50sp2.NewOrderSingle fixNewOrderSingle = NewOrderSingleFactory.fromDomainNewOrderSingle(newOrderSingle);
            Session.sendToTarget(fixNewOrderSingle, sessionID);
        } else {
            log.error("FIX Session is not logged on");
        }
    }
}
