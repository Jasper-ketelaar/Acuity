package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.bot_control.services.messaging.BotControlMessagingService;
import com.acuitybotting.db.arango.bot_control.domain.BotInstance;
import com.acuitybotting.db.arango.bot_control.repositories.BotInstanceRepository;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
public class BotControlManagementService {

    private final BotInstanceRepository botInstanceRepository;
    private final BotControlMessagingService messagingService;

    @Autowired
    public BotControlManagementService(BotInstanceRepository botInstanceRepository, BotControlMessagingService messagingService) {
        this.botInstanceRepository = botInstanceRepository;
        this.messagingService = messagingService;
    }

    public BotInstance register() {
        BotInstance botInstance = new BotInstance();
        String authKey = generateAuthKey();
        botInstance.setAuth(authKey);
        botInstance.setKey(authKey);
        return botInstanceRepository.save(botInstance);
    }

    public boolean heartbeat(String id) {
        botInstanceRepository.updateHeartbeat(id, System.currentTimeMillis());
        return true;
    }

    private String generateAuthKey(){
        return "";
    }

    public CreateQueueResult requestMessagingQueue(String ip) {
        return messagingService.createQueue("", ip);
    }
}
