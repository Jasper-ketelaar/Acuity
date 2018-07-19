package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.bot_control.domain.ScriptStorageRequest;
import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitChannel;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.listeners.adapters.MessagingChannelAdapter;
import com.acuitybotting.data.flow.messaging.services.client.listeners.adapters.MessagingClientAdapter;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.identity.RoutingUtil;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.UserDefinedDocumentRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
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

    private Gson gson = new Gson();

    private final UserDbService userDbService;

    @Autowired
    public BotControlRabbitService(ApplicationEventPublisher publisher, UserDbService userDbService) {
        this.publisher = publisher;
        this.userDbService = userDbService;
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


                            channel.consumeQueue("acuitybotting.work.script-storage.request", false, false);
                        }

                        @Override
                        public void onMessage(MessagingChannel channel, Message message) {
                            MessageEvent messageEvent = new MessageEvent()
                                    .setChannel(channel)
                                    .setMessage(message);
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


    @EventListener
    public void handleScriptStorageRequest(MessageEvent messageEvent){
        System.out.println("Got message event: " + messageEvent.getRouting());
        if (messageEvent.getRouting().endsWith(".services.script-storage.request")){
            String userId = RoutingUtil.routeToUserId(messageEvent.getRouting());
            userDbService.handle(messageEvent, gson.fromJson(messageEvent.getMessage().getBody(), ScriptStorageRequest.class), userId);
            messageEvent.getChannel().acknowledge(messageEvent.getMessage());
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        connect();
    }
}
