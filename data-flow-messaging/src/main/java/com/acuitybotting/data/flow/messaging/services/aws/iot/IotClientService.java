package com.acuitybotting.data.flow.messaging.services.aws.iot;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.acuitybotting.data.flow.messaging.services.interfaces.MessageConsumer;
import com.acuitybotting.data.flow.messaging.services.interfaces.MessagingClient;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/6/2018.
 */
@Slf4j
public class IotClientService implements MessagingClient {

    private Consumer<Throwable> exceptionHandler = throwable -> log.error("Error in Iot", throwable);
    private List<Consumer<Message>> appenders = new ArrayList<>();
    private Map<String, MessageFuture> messageCallbacks = new HashMap<>();
    private Gson gson = new Gson();

    private AWSIotMqttClient client;

    @Override
    public void start(String endpoint, String clientId, String access, String secret, String sessionToken) {
        client = new AWSIotMqttClient(endpoint, clientId, access, secret, sessionToken);
        try {
            client.connect();
        } catch (AWSIotException e) {
            getExceptionHandler().accept(e);
        }
    }

    @Override
    public Optional<MessageFuture> send(String targetExchange, String targetQueue, String localQueue, String futureId, String body) {
        Map<String, String> attributeValueMap = new HashMap<>();

        if (futureId != null) {
            attributeValueMap.put(FUTURE_ID, futureId);
        }

        MessageFuture future = EMPTY_MESSAGE_FUTURE;
        if (localQueue != null) {
            String id = UUID.randomUUID().toString().replaceAll("\\.", "-");
            future = new MessageFuture();
            future.whenComplete((message, throwable) -> messageCallbacks.remove(futureId));
            messageCallbacks.put(id, future);
            attributeValueMap.put(RESPONSE_ID, id);
            attributeValueMap.put(RESPONSE_QUEUE, localQueue);
        }

        try {
            Message message = new Message();
            message.setAttributes(attributeValueMap);
            message.setBody(body);

            client.publish(new AWSIotMessage(targetQueue, AWSIotQos.QOS0, gson.toJson(message)));
        } catch (AWSIotException e) {
            exceptionHandler.accept(e);
            future = null;
        }

        return Optional.ofNullable(future);
    }

    @Override
    public MessageConsumer consume(String queue) {
        return new IotMessageConsumer(this, queue);
    }

    @Override
    public boolean delete(Message message) {
        return false;
    }

    @Override
    public MessageFuture getMessageFuture(String id) {
        return messageCallbacks.get(id);
    }

    @Override
    public List<Consumer<Message>> getMessageAppenders() {
        return appenders;
    }

    @Override
    public Consumer<Throwable> getExceptionHandler() {
        return exceptionHandler;
    }

    public AWSIotMqttClient getClient() {
        return client;
    }

    public Gson getGson() {
        return gson;
    }
}
