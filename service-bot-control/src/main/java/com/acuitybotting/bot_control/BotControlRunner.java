package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.services.managment.BotControlManagementService;
import com.acuitybotting.data.flow.messaging.services.aws.iot.IotAuthenticationService;
import com.acuitybotting.data.flow.messaging.services.aws.iot.IotClientService;
import com.acuitybotting.data.flow.messaging.services.aws.iot.domain.RegisterResponse;
import com.acuitybotting.data.flow.messaging.services.sqs.client.util.HttpUtil;
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
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Component
@PropertySource("classpath:iot.credentials")
public class BotControlRunner implements CommandLineRunner{

    @Value("${rspeer.token}")
    private String token;

    private final BotControlManagementService managementService;

    @Autowired
    public BotControlRunner(BotControlManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public void run(String... strings) throws Exception {
        try {

            RegisterResponse registerResponse = IotAuthenticationService.authenticate(token).orElse(null);

            String userId = registerResponse.getAssumedRoleUser().getAssumedRoleId().split(":")[1];

            System.out.println("UserId: " + userId);

            String clientEndpoint = "a2i158467e5k2v.iot.us-east-1.amazonaws.com";
            String clientId = "client1";

            IotClientService iotClientService = new IotClientService();

            iotClientService.start(clientEndpoint, clientId,
                    registerResponse.getCredentials().getAccessKeyId(),
                    registerResponse.getCredentials().getSecretAccessKey(),
                    registerResponse.getCredentials().getSessionToken()
            );

            iotClientService.consume("user/" + userId + "/bot/" + clientId)
                    .withCallback(System.out::println)
                    .start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
