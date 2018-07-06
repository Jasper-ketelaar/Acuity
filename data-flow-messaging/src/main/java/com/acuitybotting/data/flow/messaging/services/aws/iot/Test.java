package com.acuitybotting.data.flow.messaging.services.aws.iot;

import com.acuitybotting.data.flow.messaging.services.sqs.client.util.HttpUtil;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        try {
            Gson gson = new Gson();

            String authUrl = "https://7ja4dnkku1.execute-api.us-east-1.amazonaws.com/Prod/registerIotConnection";

            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");

            String body = gson.toJson(Collections.singletonMap("token", ""));
            String response = HttpUtil.makeRequest("POST", headers, authUrl, null, body);

            RegisterResponse registerResponse = gson.fromJson(response, RegisterResponse.class);
            System.out.println("Response: " + registerResponse);

            String userId = registerResponse.getAssumedRoleUser().getAssumedRoleId().split(":")[1];

            System.out.println("UserId: " + userId);

            String clientEndpoint = "a2i158467e5k2v.iot.us-east-1.amazonaws.com";
            String clientId = "client1";

            AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId,
                    registerResponse.getCredentials().getAccessKeyId(),
                    registerResponse.getCredentials().getSecretAccessKey(),
                    registerResponse.getCredentials().getSessionToken()
            );

            try {
                client.connect();
                client.subscribe(new AWSIotTopic("user/" + userId + "/bot/" + clientId){
                    @Override
                    public void onMessage(AWSIotMessage message) {
                        System.out.println("Got message: " + new String(message.getPayload()));
                    }
                });
            } catch (AWSIotException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
