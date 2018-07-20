package com.acuitybotting.data.flow.messaging.services.client;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.listeners.MessagingChannelListener;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

import static com.acuitybotting.data.flow.messaging.services.client.MessagingClient.RESPONSE_ID;
import static com.acuitybotting.data.flow.messaging.services.client.MessagingClient.RESPONSE_QUEUE;

/**
 * Created by Zachary Herridge on 7/2/2018.
 */
public interface MessagingChannel {

    default MessagingChannel consumeQueue(String queue, boolean createQueue, boolean autoAcknowledge) throws RuntimeException{
        return bind(null, null, queue, createQueue, autoAcknowledge);
    }

    default MessagingChannel bindQueueToExchange(String queue, String exchange, String routing) throws RuntimeException {
        return bind(exchange, routing, queue, false, false);
    }

    MessagingChannel stopConsuming(String queue) throws RuntimeException;

    MessagingChannel bind(String exchange, String routing, String queue, boolean createQueue, boolean autoAcknowledge) throws RuntimeException;

    MessagingChannel close() throws RuntimeException;

    MessagingClient getClient();

    void acknowledge(Message message) throws RuntimeException;

    default void sendToQueue(String queue, String body) throws RuntimeException {
        send("", queue, body);
    }

    default Future<MessageEvent> sendToQueue(String queue, String localQueue, String body) throws RuntimeException {
        return send("", queue, localQueue, body);
    }

    default void send(String exchange, String routingKey, String body) throws RuntimeException {
        send(exchange, routingKey, null, null, body);
    }

    default Future<MessageEvent> send(String exchange, String routingKey, String localQueue, String body) throws RuntimeException {
        return send(exchange, routingKey, localQueue, null, body);
    }

    default void respond(Message message, String body) throws RuntimeException {
        respond(message, null, body);
    }

    default Future<MessageEvent> respond(Message message, String localQueue, String body) throws RuntimeException {
        String responseTopic = message.getAttributes().get(RESPONSE_QUEUE);
        String responseId = message.getAttributes().get(RESPONSE_ID);

        Objects.requireNonNull(responseTopic);
        Objects.requireNonNull(responseId);

        return send("", responseTopic, localQueue, responseId, body);
    }

    Future<MessageEvent> send(String targetExchange, String targetRouting, String localQueue, String futureId, String body) throws RuntimeException;

    MessageFuture getMessageFuture(String id);

    List<MessagingChannelListener> getListeners();

    void connect();
}
