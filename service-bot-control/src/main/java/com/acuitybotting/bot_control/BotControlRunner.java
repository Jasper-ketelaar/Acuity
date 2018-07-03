package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.services.managment.BotControlManagementService;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.BotInstanceRepository;
import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Component
public class BotControlRunner implements CommandLineRunner{

    private final BotControlManagementService managementService;
    private final AcuityJwtService jwtService;
    private final BotInstanceRepository repository;

    @Autowired
    public BotControlRunner(BotControlManagementService managementService, AcuityJwtService jwtService, BotInstanceRepository repository) {
        this.managementService = managementService;
        this.jwtService = jwtService;
        this.repository = repository;
    }

    @Override
    public void run(String... strings) throws Exception {

    }
}
