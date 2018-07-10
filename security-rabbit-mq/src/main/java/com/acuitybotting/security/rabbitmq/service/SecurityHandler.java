package com.acuitybotting.security.rabbitmq.service;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessageConsumer;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitClient;
import com.acuitybotting.security.rabbitmq.service.handler.AuthBackend;
import com.acuitybotting.security.rabbitmq.domain.ResourcePermission;
import com.acuitybotting.security.rabbitmq.domain.ResourceType;
import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.function.BiConsumer;

/**
 * Created by Zachary Herridge on 7/10/2018.
 */
@Slf4j
public class SecurityHandler implements BiConsumer<MessageConsumer, Message> {

    private AuthBackend authBackend;

    public SecurityHandler(AuthBackend authBackend) {
        this.authBackend = authBackend;
    }

    @Override
    public void accept(MessageConsumer messageConsumer, Message message) {
        log.info("Got request: " + message);

        String action = message.getAttributes().get("header.action");

        byte[] response = new byte[0];
        if ("login".equals(action)) {
            String username = message.getAttributes().get("header.username");
            String password = message.getAttributes().get("header.password");

            response = bytes(authBackend.login(username, password).toResult());
        } else if ("check_vhost".equals(action)) {
            String username = message.getAttributes().get("header.username");
            String vhost = message.getAttributes().get("header.vhost");

            response = bool(authBackend.checkVhost(username, vhost));
        } else if ("check_resource".equals(action)) {
            String username = message.getAttributes().get("header.username");
            String vhost = message.getAttributes().get("header.vhost");
            String name = message.getAttributes().get("header.name");
            String resource = message.getAttributes().get("header.resource");
            String permission = message.getAttributes().get("header.permission");

            response = bool(authBackend.checkResource(
                    username,
                    vhost,
                    name,
                    ResourceType.valueOf(resource.toUpperCase()),
                    ResourcePermission.valueOf(permission.toUpperCase())
            ));
        } else if ("check_topic".equals(action)) {
            String username = message.getAttributes().get("header.username");
            String vhost = message.getAttributes().get("header.vhost");
            String name = message.getAttributes().get("header.name");
            String resource = message.getAttributes().get("header.resource");
            String permission = message.getAttributes().get("header.permission");
            String routeKey = message.getAttributes().get("header.routing_key");

            response = bool(authBackend.checkTopic(
                    username,
                    vhost,
                    name,
                    ResourceType.valueOf(resource.toUpperCase()),
                    ResourcePermission.valueOf(permission.toUpperCase()),
                    routeKey));
        }

        log.info("Request result '{}'", new String(response));

        String replyTo = message.getAttributes().get("properties.reply-to");
        String correlationId = message.getAttributes().get("properties.correlation-id");

        AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder().correlationId(correlationId).build();
        RabbitClient client = (RabbitClient) messageConsumer.getClient();
        try {
            client.getChannel().basicPublish("", replyTo, replyProps, response);
        } catch (IOException e) {
            log.error("Error during reply.", e);
        }
    }

    private byte[] bytes(String s) {
        try {
            return s.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] bool(boolean b) {
        return bytes(b ? "allow" : "deny");
    }
}
