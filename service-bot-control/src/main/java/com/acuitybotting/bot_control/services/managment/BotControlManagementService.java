package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.db.arango.bot_control.domain.BotInstance;
import com.acuitybotting.db.arango.bot_control.repositories.BotInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public BotInstance register() {
        BotInstance botInstance = new BotInstance();
        botInstance.setAuth(generateAuth());
        return botInstanceRepository.save(botInstance);
    }

    public boolean heartbeat(String id) {
        botInstanceRepository.updateHeartbeat(id, System.currentTimeMillis());
        return true;
    }

    private String generateAuth(){
        return "";
    }
}
