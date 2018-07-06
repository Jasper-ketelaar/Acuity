package com.acuitybotting.data.flow.messaging.services.aws.iot;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotTopic;

public class Test {

    public static void main(String[] args) {
        String clientEndpoint = "a2i158467e5k2v.iot.us-east-1.amazonaws.com";
        String clientId = "testClientID";

        AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId,
                "",
                "");

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
}
