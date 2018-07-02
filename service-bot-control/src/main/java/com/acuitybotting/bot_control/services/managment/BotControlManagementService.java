package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.db.arango.acuity.bot_control.domain.BotInstance;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.BotInstanceRepository;
import com.acuitybotting.security.acuity.aws.secrets.AwsSecretService;
import com.acuitybotting.security.acuity.aws.secrets.domain.AccessKeyCredentials;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
public class BotControlManagementService {

    private final BotInstanceRepository botInstanceRepository;
    private final AwsSecretService secretService;

    @Autowired
    public BotControlManagementService(BotInstanceRepository botInstanceRepository, AwsSecretService secretService) {
        this.botInstanceRepository = botInstanceRepository;

        this.secretService = secretService;
    }

    public BotInstance register(String principalKey, String remoteIp) {
        if (principalKey == null) return null;

        BotInstance botInstance = new BotInstance();
        botInstance.setPrincipalKey(principalKey);
        botInstance.setConnectionTime(System.currentTimeMillis());

        return botInstanceRepository.save(botInstance);
    }

    public boolean heartbeat(String id) {
        botInstanceRepository.updateHeartbeat(id, System.currentTimeMillis());
        return true;
    }
}
