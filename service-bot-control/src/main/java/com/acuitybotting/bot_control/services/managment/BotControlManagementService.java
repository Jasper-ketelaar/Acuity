package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.bot_control.services.messaging.BotControlMessagingService;
import com.acuitybotting.db.arango.bot_control.domain.BotInstance;
import com.acuitybotting.db.arango.bot_control.repositories.BotInstanceRepository;
import com.acuitybotting.security.acuity.aws.secrets.AwsSecretService;
import com.acuitybotting.security.acuity.aws.secrets.domain.AccessKeyCredentials;
import com.acuitybotting.security.acuity.web.AcuityWebSecurity;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
public class BotControlManagementService {

    private final BotInstanceRepository botInstanceRepository;
    private final BotControlMessagingService messagingService;
    private final AwsSecretService secretService;

    @Autowired
    public BotControlManagementService(BotInstanceRepository botInstanceRepository, BotControlMessagingService messagingService, AwsSecretService secretService) {
        this.botInstanceRepository = botInstanceRepository;
        this.messagingService = messagingService;
        this.secretService = secretService;

        AccessKeyCredentials sqsAccess = secretService.getSecret("secretsmanager.us-east-1.amazonaws.com", "us-east-1", "SqsAccess", AccessKeyCredentials.class).orElseThrow(() -> new RuntimeException("Failed to acquire AccessKeyCredentials."));
        messagingService.connect(sqsAccess.getRegion(), new Credentials().withAccessKeyId(sqsAccess.getAccessKey()).withSecretKey(sqsAccess.getSecretKey()));
    }

    public BotInstance register(String principalKey, String remoteIp) {
        if (principalKey == null) return null;

        BotInstance botInstance = new BotInstance();
        botInstance.setPrincipalKey(principalKey);
        botInstance.setConnectionTime(System.currentTimeMillis());

        BotInstance save = botInstanceRepository.save(botInstance);
        if (save.getKey() != null){
            CreateQueueResult queue = messagingService.getQueueService().createQueue("acuity-bot-client-" + save.getKey() + ".fifo", remoteIp);
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

    public boolean updateQueuePolicy(String princiapKey, String instanceKey, String ip) {
        BotInstance botInstance = botInstanceRepository.findByPrincipalKeyAndKey(princiapKey, instanceKey).orElseThrow(() -> new RuntimeException("Bot instance not found."));
        String queueUrl = botInstance.getQueueUrl();
        messagingService.getQueueService().updateQueuePolicy(queueUrl, ip);
        return true;
    }
}
