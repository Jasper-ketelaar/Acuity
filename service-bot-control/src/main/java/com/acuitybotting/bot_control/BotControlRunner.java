package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.services.managment.BotControlManagementService;
import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import com.acuitybotting.security.acuity.aws.cognito.CognitoAuthenticationService;
import com.acuitybotting.security.acuity.aws.cognito.domain.CognitoConfiguration;
import com.acuitybotting.security.acuity.aws.cognito.domain.CognitoTokens;
import com.acuitybotting.bot_control.services.messaging.BotControlMessagingService;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.BotInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Component
public class BotControlRunner implements CommandLineRunner{

    private final BotControlMessagingService service;
    private final BotControlManagementService managementService;
    private final CognitoAuthenticationService cognitoAuthenticationService;
    private final AcuityJwtService jwtService;
    private final BotInstanceRepository repository;

    @Autowired
    public BotControlRunner(BotControlMessagingService service, BotControlManagementService managementService, CognitoAuthenticationService cognitoAuthenticationService, AcuityJwtService jwtService, BotInstanceRepository repository) {
        this.service = service;
        this.managementService = managementService;
        this.cognitoAuthenticationService = cognitoAuthenticationService;
        this.jwtService = jwtService;
        this.repository = repository;
    }

    @Override
    public void run(String... strings) throws Exception {
        cognitoAuthenticationService.setCognitoConfiguration(
                CognitoConfiguration.builder()
                        .poolId("us-east-1_HrbYmVhlY")
                        .clientAppId("3pgbd576sg70tsub4nh511k58u")
                        .fedPoolId("us-east-1:ff1b33f4-7f66-47a5-b7ff-9696b0e1fb52")
                        .customDomain("acuitybotting")
                        .region("us-east-1")
                        .redirectUrl("https://rspeer.org/")
                        .build()
        );

        CognitoTokens cognitoTokens = cognitoAuthenticationService.login("Zach", System.getenv("CognitoPassword")).orElse(null);
        System.out.println();

 /*       cognitoAuthenticationService.setCognitoConfiguration(
                CognitoConfiguration.builder()
                        .poolId("us-east-1_HrbYmVhlY")
                        .clientAppId("3pgbd576sg70tsub4nh511k58u")
                        .fedPoolId("us-east-1:ff1b33f4-7f66-47a5-b7ff-9696b0e1fb52")
                        .customDomain("acuitybotting")
                        .region("us-east-1")
                        .redirectUrl("https://rspeer.org/")
                        .build()
        );

        CognitoTokens cognitoTokens = cognitoAuthenticationService.login("Zach", System.getenv("CognitoPassword")).orElse(null);
        Credentials credentials = cognitoAuthenticationService.getCredentials(cognitoTokens).orElse(null);

        service.connect("us-east-1", credentials);

        String q1Url = service.getQueueService().createQueue("q1.fifo").getQueueUrl();
        service.getClientService().consumeQueue(q1Url, message -> {
            System.out.println("q1: " + message.getBody());
            service.getClientService().respondToMessage(message, "echo: " + message.getBody());
        });

        String q2Url = service.getQueueService().createQueue("q2.fifo").getQueueUrl();
        service.getClientService().consumeQueue(q2Url);
        service.getClientService().sendMessage(q1Url, q2Url, "Hello server 1.").whenComplete((message, throwable) -> {
            System.out.println("q2: " + message.getBody());
        });
        service.getClientService().sendMessage(q1Url, q2Url, "Hello server 2.").whenComplete((message, throwable) -> {
            System.out.println("q2: " + message.getBody());
        });
*/
    }
}
