package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.services.messaging.BotControlMessagingService;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Component
public class BotControlRunner implements CommandLineRunner{

    private final BotControlMessagingService service;

    @Autowired
    public BotControlRunner(BotControlMessagingService service) {
        this.service = service;
    }

    @Override
    public void run(String... strings) throws Exception {
        service.getSQS().createQueue(new CreateQueueRequest().withQueueName("myFQ.fifo").withAttributes(Collections.singletonMap("FifoQueue", "true")));
    }
}
