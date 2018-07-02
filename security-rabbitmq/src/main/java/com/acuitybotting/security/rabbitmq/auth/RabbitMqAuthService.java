package com.acuitybotting.security.rabbitmq.auth;

import com.acuitybotting.data.flow.messaging.services.rabbit.client.RabbitClientService;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class RabbitMqAuthService {

    private RabbitClientService rabbitClientService;
    private RabbitMqAuthBackend authBackend;

    @Value("${rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.port}")
    private int port;

    @Value("${rabbitmq.vhost}")
    private String vhost;

    @Value("${rabbitmq.username}")
    private String username;

    @Value("${rabbitmq.password}")
    private String password;

    public void start() {
        rabbitClientService = new RabbitClientService(){
            @Override
            public void onConnect(Connection connection, Channel channel) {
                addQueues();
            }
        };
        rabbitClientService.start(vhost, host, port, username, password);
    }

    private void addQueues(){
        try {

            AMQP.Queue.DeclareOk rcpTest = rabbitClientService.getChannel().queueDeclare("rcpTest", false, true, true, null);
            rabbitClientService.getChannel().basicConsume(rcpTest.getQueue(), new DefaultConsumer(rabbitClientService.getChannel()){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                            .Builder()
                            .correlationId(properties.getCorrelationId())
                            .build();


                    String s = new LoginResult(true, new String[]{"administrator"}).toString();

                    rabbitClientService.getChannel().basicPublish( "", properties.getReplyTo(), replyProps, s.getBytes("UTF-8"));
                }
            });
            rabbitClientService.getChannel().queueBind(rcpTest.getQueue(), "authentication", "");
        } catch (IOException e) {
            log.error("Error during auth setup.", e);
        }
    }
}
