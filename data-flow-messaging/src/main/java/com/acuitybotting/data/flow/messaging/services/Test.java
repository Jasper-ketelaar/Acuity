package com.acuitybotting.data.flow.messaging.services;

import com.acuitybotting.data.flow.messaging.services.client.MessageConsumer;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.listener.MessagingClientAdapter;

/**
 * Created by Zachary Herridge on 7/10/2018.
 */
public class Test {

    public static void main(String[] args) {
        RabbitClient rabbitClient = new RabbitClient();
        rabbitClient.auth("68.46.70.47", "root", "");

        rabbitClient.getListeners().add(new MessagingClientAdapter(){
            @Override
            public void onConnect() {
                MessageConsumer messageConsumer = rabbitClient.consume().withAutoAcknowledge(false).withCallback((source, message) -> {
                    System.out.println(message);
                    source.acknowledge(message);
                }).start();
                rabbitClient.send(messageConsumer.getQueue(), "{\"body\" : \"sup my dude\"}");
            }
        });

        rabbitClient.connect();

        while (true){

        }
    }

}
