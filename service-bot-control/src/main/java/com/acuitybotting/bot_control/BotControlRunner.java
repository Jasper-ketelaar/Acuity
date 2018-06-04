package com.acuitybotting.bot_control;

import com.acuitybotting.aws.security.cognito.CognitoService;
import com.acuitybotting.aws.security.cognito.domain.CognitoConfig;
import com.acuitybotting.aws.security.cognito.domain.CognitoLoginResult;
import com.acuitybotting.bot_control.services.messaging.BotControlMessagingService;
import com.acuitybotting.db.arango.bot_control.repositories.BotInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Component
public class BotControlRunner implements CommandLineRunner{

    private final BotControlMessagingService service;
    private final CognitoService cognitoService;
    private final BotInstanceRepository repository;

    @Autowired
    public BotControlRunner(BotControlMessagingService service, CognitoService cognitoService, BotInstanceRepository repository) {
        this.service = service;
        this.cognitoService = cognitoService;
        this.repository = repository;
    }

    @Override
    public void run(String... strings) throws Exception {
        CognitoConfig acuitybotting = CognitoConfig.builder()
                .poolId("us-east-1_HrbYmVhlY")
                .clientappId("3pgbd576sg70tsub4nh511k58u")
                .fedPoolId("us-east-1:ff1b33f4-7f66-47a5-b7ff-9696b0e1fb52")
                .customDomain("acuitybotting")
                .region("us-east-1")
                .redirectUrl("https://rspeer.org/")
                .build();

        Optional<CognitoLoginResult> zach = cognitoService.login(acuitybotting, "Zach", "");
        System.out.println();
    }
}
