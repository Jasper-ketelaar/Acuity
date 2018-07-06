package com.acuitybotting.data.flow.messaging.services.aws.iot;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.aws.iot.client.IotClient;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.acuitybotting.data.flow.messaging.services.interfaces.MessageConsumer;
import com.acuitybotting.data.flow.messaging.services.interfaces.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.interfaces.MessagingClientListener;
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
    private Gson gson = new Gson();

    private IotClient client;

    private List<MessagingClientListener> listeners = new ArrayList<>();
    private Map<String, MessageFuture> messageCallbacks = new HashMap<>();

    @Override
    public void auth(String endpoint, String clientId, String access, String secret, String sessionToken)  {
        if (client == null){
            if (sessionToken != null) client = new IotClient(endpoint, clientId, access, secret, sessionToken);
            else client = new IotClient(endpoint, clientId, access, secret);
        }
        else {
            client.updateCredentials(access, secret, sessionToken);
        }
    }

    @Override
    public void connect() throws Exception {
        client.connect();
    }

    @Override
    public Optional<MessageFuture> send(String targetTopic, String localTopic, String futureId, String body) {
        Map<String, String> messageAttributes = new HashMap<>();

        String generatedId = null;

        if (futureId != null) messageAttributes.put(FUTURE_ID, futureId);

        MessageFuture future = null;
        if (localTopic != null) {
            generatedId = generateId();
            future = new MessageFuture();
            future.whenComplete((message, throwable) -> messageCallbacks.remove(futureId));
            messageCallbacks.put(generatedId, future);
            messageAttributes.put(RESPONSE_ID, generatedId);
            messageAttributes.put(RESPONSE_TOPIC, localTopic);
        }

        try {
            Message message = new Message();
            message.setId(generateId());
            message.setAttributes(messageAttributes);
            message.setBody(body);

            for (MessagingClientListener listener : listeners) {
                listener.onMessageSend(message);
            }

            client.publish(new AWSIotMessage(targetTopic, AWSIotQos.QOS0, gson.toJson(message)));

            return Optional.ofNullable(future);
        } catch (AWSIotException e) {
            exceptionHandler.accept(e);
        }

        if (generatedId != null) messageCallbacks.remove(generatedId);
        return Optional.empty();
    }

    private String generateId(){
        return UUID.randomUUID().toString().replaceAll("\\.", "");
    }

    @Override
    public MessageConsumer consume(String topic) {
        return new IotMessageConsumer(this, topic);
    }

    @Override
    public MessageFuture getMessageFuture(String id) {
        return messageCallbacks.get(id);
    }

    @Override
    public List<MessagingClientListener> getListeners() {
        return listeners;
    }

    @Override
    public Consumer<Throwable> getExceptionHandler() {
        return exceptionHandler;
    }

    public IotClient getClient() {
        return client;
    }

    public Gson getGson() {
        return gson;
    }
}
