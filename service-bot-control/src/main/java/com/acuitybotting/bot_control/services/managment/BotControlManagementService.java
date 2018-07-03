package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.db.arango.acuity.bot_control.domain.BotInstance;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.BotInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
public class BotControlManagementService {

    private final BotInstanceRepository botInstanceRepository;

    @Autowired
    public BotControlManagementService(BotInstanceRepository botInstanceRepository) {
        this.botInstanceRepository = botInstanceRepository;
    }

    public BotInstance register(String principalKey, String remoteIp) {
        if (principalKey == null) return null;

        BotInstance botInstance = new BotInstance();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("connectIp", remoteIp);

        botInstance.setAttributes(attributes);
        botInstance.setPrincipalKey(principalKey);
        botInstance.setConnectionTime(System.currentTimeMillis());

        return botInstanceRepository.save(botInstance);
    }
}
