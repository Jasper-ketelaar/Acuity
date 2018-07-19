package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.db.arango.acuity.bot_control.domain.BotInstance;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.BotInstanceRepository;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityIdentityService;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
public class BotControlManagementService {

    private final AcuityIdentityService acuityIdentityService;
    private final BotInstanceRepository botInstanceRepository;

    @Autowired
    public BotControlManagementService(AcuityIdentityService acuityIdentityService, BotInstanceRepository botInstanceRepository) {
        this.acuityIdentityService = acuityIdentityService;
        this.botInstanceRepository = botInstanceRepository;
    }

    public BotInstance register(AcuityPrincipal acuityPrincipal, String remoteIp) {
        Objects.requireNonNull(acuityPrincipal);

        BotInstance botInstance = new BotInstance();

        Map<String, Object> attributes = new HashMap<>();
        if (remoteIp != null) attributes.put("connectIp", remoteIp);

        botInstance.setAttributes(attributes);
        botInstance.setPrincipalKey(acuityPrincipal.getKey());
        botInstance.setConnectionTime(System.currentTimeMillis());

        return botInstanceRepository.save(botInstance);
    }

    private String key(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
