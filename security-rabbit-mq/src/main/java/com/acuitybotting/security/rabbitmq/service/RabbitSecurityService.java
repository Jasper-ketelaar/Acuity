package com.acuitybotting.security.rabbitmq.service;

import com.acuitybotting.data.flow.messaging.services.client.MessageConsumer;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.listener.MessagingClientAdapter;
import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import com.acuitybotting.security.rabbitmq.service.handler.AuthBackendImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 7/10/2018.
 */
@Service
public class RabbitSecurityService {

    private AcuityJwtService acuityJwtService;

    private RabbitClient rabbitClient = new RabbitClient();
    private MessageConsumer consumer;

    @Value("${rabbit.host}")
    private String host;

    @Value("${rabbit.username}")
    private String username;

    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public RabbitSecurityService(AcuityJwtService acuityJwtService) {
        this.acuityJwtService = acuityJwtService;
    }

    public void start() throws RuntimeException {
        rabbitClient.auth(host, username, password);

        rabbitClient.getListeners().add(new MessagingClientAdapter(){
            @Override
            public void onConnect() {
                consumer = rabbitClient
                        .consume()
                        .withCallback(new SecurityHandler(new AuthBackendImpl(acuityJwtService)))
                        .withAutoAcknowledge(true)
                        .start()
                        .bind("authentication", "*");
            }
        });

        rabbitClient.connect();
    }
}
