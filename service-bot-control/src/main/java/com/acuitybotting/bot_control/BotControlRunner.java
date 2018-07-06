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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Component
@PropertySource("classpath:iot.credentials")
public class BotControlRunner implements CommandLineRunner{

    String policy = "{\n" +
            "    \"Version\": \"2012-10-17\",\n" +
            "    \"Statement\": [\n" +
            "        {\n" +
            "            \"Sid\": \"VisualEditor0\",\n" +
            "            \"Effect\": \"Allow\",\n" +
            "            \"Action\": \"iot:Connect\",\n" +
            "            \"Resource\": \"*\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"Sid\": \"VisualEditor1\",\n" +
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

    @Value("${iot.region}")
    private String region;

    @Value("${iot.access}")
    private String accessKey;

    @Value("${iot.secret}")
    private String secretKey;

    @Value("${iot.endpoint}")
    private String endpoint;


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
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                    .build();

            String clientId = "client1";
            String userId = "zach";
            String topic = "user/" + userId + "/bot/" + clientId;
            String localPolicy = policy.replaceAll("<USER_ID>", userId);

            AssumeRoleRequest roleRequest = new AssumeRoleRequest();
            roleRequest.setRoleArn("arn:aws:iam::604080725100:role/IotUserConnection");
            roleRequest.setRoleSessionName(userId);
            roleRequest.withPolicy(localPolicy);

            AssumeRoleResult assumeRoleWithWebIdentityResult = stsClient.assumeRole(roleRequest);

            System.out.println("Role: " + assumeRoleWithWebIdentityResult.getAssumedRoleUser().getArn());
            System.out.println("Policy: " + localPolicy);
            System.out.println("Topic: '" + topic + "'");

            AWSIotMqttClient client = new AWSIotMqttClient(endpoint, clientId,
                    assumeRoleWithWebIdentityResult.getCredentials().getAccessKeyId(),
                    assumeRoleWithWebIdentityResult.getCredentials().getSecretAccessKey(),
                    assumeRoleWithWebIdentityResult.getCredentials().getSessionToken()
            );

            try {
                client.connect();
                client.subscribe(new AWSIotTopic(topic){
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
