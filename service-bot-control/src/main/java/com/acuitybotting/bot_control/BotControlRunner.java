package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.services.managment.BotControlManagementService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Component
public class BotControlRunner implements CommandLineRunner{

    String policy = "{\n" +
            "    \"Version\": \"2012-10-17\",\n" +
            "    \"Statement\": [\n" +
            "        {\n" +
            "            \"Sid\": \"s2\",\n" +
            "            \"Effect\": \"Allow\",\n" +
            "            \"Action\": [\n" +
            "                \"iot:Receive\",\n" +
            "                \"iot:Subscribe\",\n" +
            "                \"iot:Publish\"\n" +
            "            ],\n" +
            "            \"Resource\": [\n" +
            "                \"arn:aws:iot:*:*:topicfilter/user/<USER_ID>/bot/*\",\n" +
            "                \"arn:aws:iot:*:*:topic/user/<USER_ID>/bot/*\"\n" +
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
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("", "")))
                    .build();

            AmazonCognitoIdentity identityClient = AmazonCognitoIdentityClient.builder()
                    .withRegion("us-east-1")
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("", "")))
                    .build();

            String userId = "zach";

            GetOpenIdTokenForDeveloperIdentityRequest openIdRequest = new GetOpenIdTokenForDeveloperIdentityRequest();
            openIdRequest.setIdentityPoolId("us-east-1:971873e2-32f4-4352-968c-283ab6bebde6");
            openIdRequest.addLoginsEntry("login.acuitybotting", userId);


            GetOpenIdTokenForDeveloperIdentityResult openId = identityClient.getOpenIdTokenForDeveloperIdentity(openIdRequest);

            AssumeRoleWithWebIdentityRequest assumeRoleRequest = new AssumeRoleWithWebIdentityRequest();
            assumeRoleRequest.setWebIdentityToken(openId.getToken());
            assumeRoleRequest.setRoleArn("arn:aws:iam::604080725100:role/IotUserRole");
            assumeRoleRequest.setRoleSessionName(userId);


            AssumeRoleRequest assumeRoleRequest1 = new AssumeRoleRequest();
            assumeRoleRequest1.setRoleArn("arn:aws:iam::604080725100:role/Iot2");
            assumeRoleRequest1.setRoleSessionName(userId);

            AssumeRoleResult assumeRoleWithWebIdentityResult = stsClient.assumeRole(assumeRoleRequest1);

            System.out.println("ID: " + assumeRoleWithWebIdentityResult.getCredentials().getAccessKeyId());
            System.out.println("KE: " + assumeRoleWithWebIdentityResult.getCredentials().getSecretAccessKey());
            System.out.println("TO: " + assumeRoleWithWebIdentityResult.getCredentials().getSessionToken());

            System.out.println("ROLE: " + assumeRoleWithWebIdentityResult.getAssumedRoleUser().getArn());

            String clientEndpoint = "a2i158467e5k2v.iot.us-east-1.amazonaws.com";
            String clientId = "testClientID";

            AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId,
                    assumeRoleWithWebIdentityResult.getCredentials().getAccessKeyId(),
                    assumeRoleWithWebIdentityResult.getCredentials().getSecretAccessKey(),
                    assumeRoleWithWebIdentityResult.getCredentials().getSessionToken()
            );

            try {
                client.connect();


                client.subscribe(new AWSIotTopic("user/zach/bot/" + clientId){
                    @Override
                    public void onMessage(AWSIotMessage message) {
                        System.out.println("Got message: " + new String(message.getPayload()));
                    }
                });
            } catch (AWSIotException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
