package com.acuitybotting.data.flow.messaging.services.sqs.client;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.acuitybotting.data.flow.messaging.services.interfaces.MessagingClient;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/3/2018.
 */
@Slf4j
public class SqsClientService implements MessagingClient {

    private Consumer<Throwable> exceptionHandler = throwable -> log.error("Error in Rabbit", throwable);
    private List<Consumer<Message>> appenders = new ArrayList<>();
    private Map<String, MessageFuture> messageCallbacks = new HashMap<>();

    private Map<String, String> headers = new HashMap<>();

    @Override
    public Optional<MessageFuture> send(String targetExchange, String targetQueue, String localQueue, String futureId, String body) {
        Map<String, String> attributeValueMap = new HashMap<>();

        if (futureId != null) {
            attributeValueMap.put(FUTURE_ID, futureId);
        }

        MessageFuture future = null;
        if (localQueue != null) {
            String id = UUID.randomUUID().toString().replaceAll("\\.", "-");
            future = new MessageFuture();
            messageCallbacks.put(id, future);
            attributeValueMap.put(RESPONSE_ID, id);
            attributeValueMap.put(RESPONSE_QUEUE, localQueue);
        }

        TreeMap<String, String> params = new TreeMap<>();
        params.put("Action", "SendMessage");
        params.put("Version", "2012-11-05");

        if (targetQueue.endsWith(".fifo") || targetQueue.endsWith(".fifo/")) {
            params.put("MessageGroupId", "channel1");
            params.put("MessageDeduplicationId", UUID.randomUUID().toString());
        }

        params.put("MessageBody", body);

        int attributeIndex = 1;
        for (Map.Entry<String, String> entry : attributeValueMap.entrySet()) {
            params.put("MessageAttribute." + attributeIndex + ".Name", entry.getKey());
            params.put("MessageAttribute." + attributeIndex + ".Value.StringValue", entry.getValue());
            attributeIndex++;
        }

        try {
            HttpUtil.get(getHeaders(), targetQueue, params);
        } catch (Exception e) {
            getExceptionHandler().accept(e);
        }

        return Optional.ofNullable(future);
    }

    @Override
    public SqsMessageConsumer consume(String queue, Consumer<Message> callback) {
        return new SqsMessageConsumer(this, queue);
    }

    @Override
    public boolean delete(Message message) {
        String request = message.getSource() + "?Action=DeleteMessage" + "&Version=" + "2012-11-05" + "&ReceiptHandle=" + HttpUtil.encode(message.getDeliveryTag());
        try {
            HttpUtil.get(getHeaders(), request, null);
            return true;
        } catch (Exception e) {
            getExceptionHandler().accept(e);
        }
        return false;
    }

    @Override
    public void start(String authHeader) {
        headers.put("User-Agent", authHeader);
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

    public Map<String, String> getHeaders() {
        return headers;
    }
}
