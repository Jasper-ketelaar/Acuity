package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.services.messaging.BotControlMessagingService;
import com.acuitybotting.db.arango.bot_control.domain.BotInstance;
import com.acuitybotting.db.arango.bot_control.repositories.BotInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Component
public class BotControlRunner implements CommandLineRunner{

    private final BotControlMessagingService service;
    private final BotInstanceRepository repository;

    @Autowired
    public BotControlRunner(BotControlMessagingService service, BotInstanceRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @Override
    public void run(String... strings) throws Exception {
        repository.updateHeartbeat("BotInstance/315701", System.currentTimeMillis());
    }
}
