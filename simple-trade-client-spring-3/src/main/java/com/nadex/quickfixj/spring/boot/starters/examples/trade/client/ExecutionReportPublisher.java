package com.nadex.quickfixj.spring.boot.starters.examples.trade.client;

import com.nadex.quickfixj.spring.boot.starters.examples.trade.client.domain.ExecutionReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExecutionReportPublisher {

    private final MessageSendingOperations<String> messageSendingOperations;
    public ExecutionReportPublisher(MessageSendingOperations<String> messageSendingOperations) {
        this.messageSendingOperations = messageSendingOperations;
    }

    public void publishExecutionReport(ExecutionReport executionReport) throws Exception {
        log.info("ExecutionReport received: {}", executionReport);
        this.messageSendingOperations.convertAndSend("/topic/messages", executionReport);
    }
}
