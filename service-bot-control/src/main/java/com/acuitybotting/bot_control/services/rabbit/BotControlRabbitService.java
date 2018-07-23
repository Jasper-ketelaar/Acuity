package com.acuitybotting.bot_control.services.rabbit;

import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.listeners.adapters.MessagingChannelAdapter;
import com.acuitybotting.data.flow.messaging.services.client.listeners.adapters.MessagingClientAdapter;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@PropertySource("classpath:general-worker-rabbit.credentials")
@Service
@Slf4j
public class BotControlRabbitService implements CommandLineRunner {

    private final ApplicationEventPublisher publisher;

    @Value("${rabbit.host}")
    private String host;
    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;

    private RabbitChannel rabbitChannel;

    @Autowired
    public BotControlRabbitService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    private void connect() {
        try {
            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.auth(host, username, password);

            rabbitClient.getListeners().add(new MessagingClientAdapter() {
                @Override
                public void onConnect(MessagingClient client) {
                    rabbitChannel = (RabbitChannel) client.createChannel();
                    rabbitChannel.getListeners().add(new MessagingChannelAdapter() {
                        @Override
                        public void onConnect(MessagingChannel channel) {
                            String localQueue = "bot-control-worker-" + ThreadLocalRandom.current().nextInt(0, 1000);
                            channel.consumeQueue(localQueue, true, true);
                            channel.bindQueueToExchange(localQueue, "amq.rabbitmq.event", "queue.#");

                            channel.consumeQueue("acuitybotting.work.acuity-db.request", false, false);
                            channel.consumeQueue("acuitybotting.work.connections", false, false);
                        }

                        @Override
                        public void onMessage(MessageEvent messageEvent) {
                            publisher.publishEvent(messageEvent);
                        }
                    });
                    rabbitChannel.connect();
                }
            });

            rabbitClient.connect();
        } catch (Throwable e) {
            log.error("Error during dashboard RabbitMQ setup.", e);
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
