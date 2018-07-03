package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.services.managment.BotControlManagementService;
import com.acuitybotting.db.arango.acuity.bot_control.domain.BotInstance;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.BotInstanceRepository;
import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Component
public class BotControlRunner implements CommandLineRunner{

    private final BotControlManagementService managementService;

    @Autowired
    public BotControlRunner(BotControlManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public void run(String... strings) throws Exception {
        AcuityPrincipal acuityPrincipal = new AcuityPrincipal();
        acuityPrincipal.setUsername("c247fa6b-5676-4012-9473-a7b2f60c8115");
        acuityPrincipal.setSub("c247fa6b-5676-4012-9473-a7b2f60c8115");
        acuityPrincipal.setRealm("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_aVWAQrVQG");
        BotInstance instance = managementService.register(acuityPrincipal, "testip");
        System.out.println(instance);
    }
}
