package com.acuitybotting.data.flow.messaging.services.interfaces;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/2/2018.
 */
public interface MessagingClient {

    String FUTURE_ID = "futureId";
    String RESPONSE_ID = "responseId";
    String RESPONSE_TOPIC = "responseTopic";

    default void send(String topic, String body){
        send(topic, null, null, body);
    }

    default Optional<MessageFuture> send(String topic, String localQueue, String body){
        return send(topic, localQueue, null, body);
    }

    default void respond(Message message, String body){
        respond(message, null, body);
    }

    default Optional<MessageFuture> respond(Message message, String localTopic, String body){
        String responseTopic = message.getAttributes().get(RESPONSE_TOPIC);
        String responseId = message.getAttributes().get(RESPONSE_ID);

        Objects.requireNonNull(responseTopic);
        Objects.requireNonNull(responseId);

        return send(responseTopic, localTopic, responseId, body);
    }

    Optional<MessageFuture> send(String targetTopic, String localTopic, String futureId, String body);

    MessageConsumer consume(String topic);

    void auth(String endpoint, String clientId, String access, String secret, String sessionToken);

    void connect() throws Exception;

    MessageFuture getMessageFuture(String id);

    List<MessagingClientListener> getListeners();

    Consumer<Throwable> getExceptionHandler();
}
