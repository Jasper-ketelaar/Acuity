package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.bot_control.services.messaging.BotControlMessagingService;
import com.acuitybotting.db.arango.bot_control.domain.BotInstance;
import com.acuitybotting.db.arango.bot_control.repositories.BotInstanceRepository;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
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

    public BotInstance register(AcuityPrincipal principal, String remoteIp) {
        if (principal == null || principal.getUsername() == null || principal.getRealm() == null) return null;
        BotInstance botInstance = new BotInstance();
        String authKey = generateAuthKey();
        botInstance.setAuthKey(authKey);

        botInstance.setRealm(principal.getRealm());
        botInstance.setPrincipal(principal.getUsername());

        BotInstance save = botInstanceRepository.save(botInstance);
        if (save != null){
            CreateQueueResult queue = messagingService.getQueueService().createQueue("bot-" + save.getKey(), remoteIp);
            if (queue != null){
                save.setQueueUrl(queue.getQueueUrl());
            }
            return botInstanceRepository.save(save);
        }

        return null;
    }

    public boolean heartbeat(String id) {
        botInstanceRepository.updateHeartbeat(id, System.currentTimeMillis());
        return true;
    }

    private String generateAuthKey(){
        return "";
    }
}
