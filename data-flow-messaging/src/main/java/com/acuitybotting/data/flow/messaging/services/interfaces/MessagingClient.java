package com.acuitybotting.data.flow.messaging.services.interfaces;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/2/2018.
 */
public interface MessagingClient {

    String FUTURE_ID = "futureId";
    String RESPONSE_ID = "responseId";
    String RESPONSE_QUEUE = "responseQueue";

    default boolean send(String queue, String body){
        return send("", queue, body);
    }

    default boolean send(String targetExchange, String targetQueue, String body){
        return send(targetExchange, targetQueue, null, null, body).isPresent();
    }

    default Optional<MessageFuture> send(String targetExchange, String queue, String localQueue, String body){
        return send(targetExchange, queue, localQueue, null, body);
    }

    default boolean respond(Message message, String body){
        return respond(message, null, body).isPresent();
    }

    default Optional<MessageFuture> respond(Message message, String localQueue, String body){
        String responseQueue = message.getAttributes().get(RESPONSE_QUEUE);
        String responseId = message.getAttributes().get(RESPONSE_ID);
        return send("", responseQueue, localQueue, responseId, body);
    }

    Optional<MessageFuture> send(String targetExchange, String targetQueue, String localQueue, String futureId, String body);

    default MessageConsumer consume(String queue){
        return consume(queue, null);
    }

    MessageConsumer consume(String queue, Consumer<Message> callback);

    boolean delete(Message message);

    void start(String vHost, String host, int port, String username, String password);

    MessageFuture getMessageFuture(String id);

    List<Consumer<Message>> getMessageAppenders();

    Consumer<Throwable> getExceptionHandler();
}
