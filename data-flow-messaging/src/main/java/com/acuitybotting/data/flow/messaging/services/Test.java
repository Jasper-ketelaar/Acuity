package com.acuitybotting.data.flow.messaging.services;

import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.listener.MessagingClientAdapter;

/**
 * Created by Zachary Herridge on 7/10/2018.
 */
public class Test {

    public static void main(String[] args) {
        String sub = "c247fa6b-5676-4012-9473-a7b2f60c8115";
        RabbitClient rabbitClient = new RabbitClient().setVirtualHost("AcuityBotting");
        rabbitClient.auth(
                "68.46.70.47",
                sub,
                ""
        );

        rabbitClient.getListeners().add(new MessagingClientAdapter(){
            @Override
            public void onConnect() {
                System.out.println("Connected");
                rabbitClient.consume("user." + sub + ".queue." + rabbitClient.generateId(), true).start();

                rabbitClient.send("acuitybotting.general", "user." + sub + ".services.walking", "hey");
            }
        });
        rabbitClient.connect();
    }

}
