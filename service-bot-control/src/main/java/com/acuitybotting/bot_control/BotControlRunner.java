package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.services.managment.BotControlManagementService;
import com.acuitybotting.db.arango.acuity.bot_control.domain.BotInstance;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.BotInstanceRepository;
import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder;
import com.amazonaws.services.cognitoidentity.model.*;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.GetFederationTokenRequest;
import com.amazonaws.services.securitytoken.model.GetFederationTokenResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Component
public class BotControlRunner implements CommandLineRunner{

    String policy = "{\n" +
            "    \"Version\": \"2012-10-17\",\n" +
            "    \"Statement\": [\n" +
            "        {\n" +
            "            \"Sid\": \"s1\",\n" +
            "            \"Effect\": \"Allow\",\n" +
            "            \"Action\": \"iot:Connect\",\n" +
            "            \"Resource\": \"*\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"Sid\": \"s2\",\n" +
            "            \"Effect\": \"Allow\",\n" +
            "            \"Action\": [\n" +
            "                \"iot:Receive\",\n" +
            "                \"iot:Subscribe\",\n" +
            "                \"iot:Publish\"\n" +
            "            ],\n" +
            "            \"Resource\": [\n" +
            "                \"arn:aws:iot:*:*:topicfilter/user/<USER_ID>/*\",\n" +
            "                \"arn:aws:iot:*:*:topic/user/<USER_ID>/*\"\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    private final BotControlManagementService managementService;

    @Autowired
    public BotControlRunner(BotControlManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public void run(String... strings) throws Exception {
        try {
            AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
                    .withRegion("us-east-1")
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("AKIAIPISVVFQRY5A2I5Q", "")))
                    .build();

            String username = "zach";

            GetFederationTokenRequest getFederationTokenRequest = new GetFederationTokenRequest();
            getFederationTokenRequest.setName(username);
            getFederationTokenRequest.setDurationSeconds(3600 * 8);

            String localPolicy = policy
                    .replaceAll("<USER_ID>", username);

            System.out.println(localPolicy);
            getFederationTokenRequest.setPolicy(localPolicy);

            GetFederationTokenResult federationToken = stsClient.getFederationToken(getFederationTokenRequest);

            AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest();
            assumeRoleRequest.withRoleArn("");

            AssumeRoleResult assumeRoleResult = stsClient.assumeRole(assumeRoleRequest);

            System.out.println("id: " + federationToken.getCredentials().getAccessKeyId());
            System.out.println("key: " + federationToken.getCredentials().getSecretAccessKey());
            System.out.println("session: " + federationToken.getCredentials().getSessionToken());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
