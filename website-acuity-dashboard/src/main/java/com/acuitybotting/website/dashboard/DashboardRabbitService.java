package com.acuitybotting.website.dashboard;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.listeners.adapters.MessagingChannelAdapter;
import com.acuitybotting.data.flow.messaging.services.client.listeners.adapters.MessagingClientAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 7/18/2018.
 */
@PropertySource("classpath:general-worker-rabbit.credentials")
@Service
@Slf4j
public class DashboardRabbitService implements CommandLineRunner {

    @Value("${rabbit.host}")
    private String host;

    @Value("${rabbit.username}")
    private String username;

    @Value("${rabbit.password}")
    private String password;

    private RabbitChannel rabbitChannel;

    private void connect(){
        try {
            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.auth(host, username, password);

            rabbitClient.getListeners().add(new MessagingClientAdapter(){
                @Override
                public void onConnect(MessagingClient client) {
                    rabbitChannel = (RabbitChannel) client.createChannel();
                    rabbitChannel.getListeners().add(new MessagingChannelAdapter(){
                        @Override
                        public void onConnect(MessagingChannel channel) {
                            channel.consumeQueue("testQueue", true, true);
                            channel.bindQueueToExchange("testQueue", "amq.rabbitmq.event", "queue.#");
                        }

                        @Override
                        public void onMessage(MessagingChannel channel, Message message) {
                            System.out.println(message);
                        }
                    });
                    rabbitChannel.connect();
                }
            });

            rabbitClient.connect();
        }
        catch (Throwable e){
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
