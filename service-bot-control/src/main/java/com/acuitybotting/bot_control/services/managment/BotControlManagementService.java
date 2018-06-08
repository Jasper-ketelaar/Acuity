package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.bot_control.services.messaging.BotControlMessagingService;
import com.acuitybotting.db.arango.bot_control.domain.BotInstance;
import com.acuitybotting.db.arango.bot_control.repositories.BotInstanceRepository;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import com.acuitybotting.security.acuity.web.AcuityWebSecurity;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
        BotInstance botInstance = new BotInstance();

        if (principal.getKey() == null) return null;
        botInstance.setPrincipalKey(principal.getKey());
        botInstance.setConnectionTime(System.currentTimeMillis());

        BotInstance save = botInstanceRepository.save(botInstance);
        if (save.getKey() != null){
            CreateQueueResult queue = messagingService.getQueueService().createQueue("bot-" + save.getKey(), remoteIp);
            if (queue != null && queue.getQueueUrl() != null){
                save.setQueueUrl(queue.getQueueUrl());
                return botInstanceRepository.save(save);
            }
        }

        return null;
    }

    public boolean heartbeat(String id) {
        botInstanceRepository.updateHeartbeat(id, System.currentTimeMillis());
        return true;
    }

    public boolean updateQueuePolicy(String instanceKey, String ip) {
        BotInstance botInstance = botInstanceRepository.findByPrincipalKeyAndKey(AcuityWebSecurity.getPrincipalKey(), instanceKey).orElseThrow(() -> new RuntimeException("Bot instance not found."));
        String queueUrl = botInstance.getQueueUrl();
        messagingService.getQueueService().updateQueuePolicy(queueUrl, ip);
        return true;
    }
}
