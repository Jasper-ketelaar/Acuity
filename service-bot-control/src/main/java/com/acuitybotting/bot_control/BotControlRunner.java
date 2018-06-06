package com.acuitybotting.bot_control;

import com.acuitybotting.aws.security.cognito.CognitoJwtService;
import com.acuitybotting.aws.security.cognito.CognitoAuthenticationService;
import com.acuitybotting.aws.security.cognito.domain.CognitoConfiguration;
import com.acuitybotting.aws.security.cognito.domain.CognitoTokens;
import com.acuitybotting.bot_control.services.messaging.BotControlMessagingService;
import com.acuitybotting.db.arango.bot_control.repositories.BotInstanceRepository;
import com.amazonaws.services.sqs.model.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Component
public class BotControlRunner implements CommandLineRunner{

    private final BotControlMessagingService service;
    private final CognitoAuthenticationService cognitoAuthenticationService;
    private final CognitoJwtService jwtService;
    private final BotInstanceRepository repository;

    @Autowired
    public BotControlRunner(BotControlMessagingService service, CognitoAuthenticationService cognitoAuthenticationService, CognitoJwtService jwtService, BotInstanceRepository repository) {
        this.service = service;
        this.cognitoAuthenticationService = cognitoAuthenticationService;
        this.jwtService = jwtService;
        this.repository = repository;
    }

    @Override
    public void run(String... strings) throws Exception {




    /*    cognitoAuthenticationService.setCognitoConfiguration(
                CognitoConfiguration.builder()
                    .poolId("us-east-1_HrbYmVhlY")
                    .clientAppId("3pgbd576sg70tsub4nh511k58u")
                    .fedPoolId("us-east-1:ff1b33f4-7f66-47a5-b7ff-9696b0e1fb52")
                    .customDomain("acuitybotting")
                    .region("us-east-1")
                    .redirectUrl("https://rspeer.org/")
                    .build()
        );

        CognitoTokens zach = cognitoAuthenticationService.login(
                "Zach",
                System.getenv("CognitoPassword")
        ).orElseThrow(() -> new RuntimeException("Failed to login."));

        DecodedJWT jwt = jwtService.decodeAndVerify(zach.getIdToken()).orElseThrow(() -> new RuntimeException("Failed to decode JWT."));
        System.out.println(jwt.getPayload());*/
    }

    private void read(String queueUrl){
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true){
                ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest();
                receiveMessageRequest.withQueueUrl(queueUrl);
                receiveMessageRequest.withMaxNumberOfMessages(10);
                receiveMessageRequest.withWaitTimeSeconds(60);
                ReceiveMessageResult receiveMessageResult = service.getSQS().receiveMessage(receiveMessageRequest);

                for (Message message : receiveMessageResult.getMessages()) {
                    System.out.println("Got message: " + message.getBody());
                    service.getSQS().deleteMessage(new DeleteMessageRequest().withQueueUrl(queueUrl).withReceiptHandle(message.getReceiptHandle()));
                }
            }
        });
    }
}
