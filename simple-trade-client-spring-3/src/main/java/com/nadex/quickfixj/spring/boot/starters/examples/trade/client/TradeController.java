package com.nadex.quickfixj.spring.boot.starters.examples.trade.client;

import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.NewOrderSingle;
import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.OrderCancel;
import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.OrderCancelReplaceRequest;
import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.from.websocket.NewOrderSingleFactory;
import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.from.websocket.OrderCancelReplaceRequestFactory;
import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.from.websocket.OrderCancelRequestFactory;
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
public class TradeController {

    public static final String FIX_SESSION_IS_NOT_LOGGED_ON = "FIX Session is not logged on";
    @Getter @Setter private SessionID sessionID;
    @Getter @Setter private boolean isSessionLoggedOn;

    public TradeController() {
    }

    @MessageMapping("/new-order-single")
    public void setNewOrderSingle(NewOrderSingle newOrderSingle) throws Exception {
        log.info("New Order Single Received: {}", newOrderSingle);

        if (isSessionLoggedOn) {
            quickfix.fix50sp2.NewOrderSingle fixNewOrderSingle = NewOrderSingleFactory.fromDomain(newOrderSingle);
            Session.sendToTarget(fixNewOrderSingle, sessionID);
        } else {
            log.error(FIX_SESSION_IS_NOT_LOGGED_ON);
        }
    }

    @MessageMapping("/order-cancel")
    public void setOrderCancel(OrderCancel orderCancelRequest) throws Exception {
        log.info("Order Cancel Received: {}", orderCancelRequest);
        if (isSessionLoggedOn) {
            //TODO
            quickfix.fix50sp2.OrderCancelRequest fixOrderCancelRequest =
                    new OrderCancelRequestFactory().fromDomain(orderCancelRequest);
            Session.sendToTarget(fixOrderCancelRequest, sessionID);
        } else {
            log.error(FIX_SESSION_IS_NOT_LOGGED_ON);
        }
    }

    @MessageMapping("/order-cancel-replace-request")
    public void setOrderCancelReplaceRequest(OrderCancelReplaceRequest orderCancelReplaceRequest) throws Exception {
        log.info("Order Cancel Replace Request Received: {}", orderCancelReplaceRequest);

        if (isSessionLoggedOn) {
            //TODO
            quickfix.fix50sp2.OrderCancelReplaceRequest fiXOrderCancelReplaceRequest =
                    new OrderCancelReplaceRequestFactory().fromDomain(orderCancelReplaceRequest);
            Session.sendToTarget(fiXOrderCancelReplaceRequest, sessionID);
        } else {
            log.error(FIX_SESSION_IS_NOT_LOGGED_ON);
        }
    }
}
