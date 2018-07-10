package com.acuitybotting.data.flow.messaging.services.client;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.listener.MessagingClientListener;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/2/2018.
 */
public interface MessagingClient {

    String FUTURE_ID = "futureId";
    String RESPONSE_ID = "responseId";
    String RESPONSE_QUEUE = "responseQueue";


    default void send(String queue, String body) throws RuntimeException {
        send("", queue, body);
    }

    default void send(String exchange, String routingKey, String body) throws RuntimeException {
        send(exchange, routingKey, null, null, body);
    }

    default Future<Message> send(String exchange, String routingKey, String localQueue, String body) throws RuntimeException {
        return send(exchange, routingKey, localQueue, null, body);
    }

    default void respond(Message message, String body) throws RuntimeException {
        respond(message, null, body);
    }

    default Future<Message> respond(Message message, String localTopic, String body) throws RuntimeException {
        String responseTopic = message.getAttributes().get(RESPONSE_QUEUE);
        String responseId = message.getAttributes().get(RESPONSE_ID);

        Objects.requireNonNull(responseTopic);
        Objects.requireNonNull(responseId);

        return send("", responseTopic, localTopic, responseId, body);
    }

    Future<Message> send(String targetExchange, String targetRouting, String localQueue, String futureId, String body) throws RuntimeException;

    default MessageConsumer consume() throws RuntimeException {
        return consume(null);
    }

    MessageConsumer consume(String queue) throws RuntimeException;

    void auth(String endpoint, String username, String password);

    void connect() throws RuntimeException;

    void acknowledge(Message message) throws RuntimeException;

    MessageFuture getMessageFuture(String id);

    List<MessagingClientListener> getListeners();

    Consumer<Throwable> getExceptionHandler();

    boolean isConnected();
}
