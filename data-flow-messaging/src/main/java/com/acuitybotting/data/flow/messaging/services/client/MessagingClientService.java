package com.acuitybotting.data.flow.messaging.services.client;

import com.acuitybotting.data.flow.messaging.services.client.message.Message;
import com.acuitybotting.data.flow.messaging.services.client.message.MessageFuture;
import com.acuitybotting.data.flow.messaging.services.client.message.MessageParser;
import com.acuitybotting.data.flow.messaging.services.client.util.HttpUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 6/7/2018.
 */
@Service
public class MessagingClientService {

    private static final String FUTURE_ID = "futureId";
    private static final String RESPONSE_ID = "responseId";
    private static final String RESPONSE_URL = "responseUrl";

    private Map<String, MessageFuture> messageCallbacks = new HashMap<>();

    private String accessKey;
    private String accessSecret;

    private int maxMessages = 3;
    private int messageTimeout = 20;
    private int visibilityTimeout = 20;

    public CompletableFuture<Message> sendMessage(String queueUrl, String localUrl, String body) throws Exception {
        return sendMessage(queueUrl, localUrl, null, body);
    }

    public void sendMessage(String queueUrl, String body) throws Exception {
        sendMessage(queueUrl, null, null, body);
    }

    public void respondToMessage(Message message, String body) throws Exception {
        respondToMessage(message, null, body);
    }

    public CompletableFuture<Message> respondToMessage(Message message, String localUrl, String body) throws Exception {
        String responseUrl = message.getAttributes().get(RESPONSE_URL);
        String responseId = message.getAttributes().get(RESPONSE_ID);
        return sendMessage(responseUrl, localUrl, responseId, body);
    }

    private CompletableFuture<Message> sendMessage(String queueUrl, String localUrl, String futureId, String body) throws Exception {
        Map<String, String> attributeValueMap = new HashMap<>();

        if (futureId != null) {
            attributeValueMap.put(FUTURE_ID, futureId);
        }

        MessageFuture future = null;
        if (localUrl != null) {
            String id = UUID.randomUUID().toString().replaceAll("\\.", "-");
            future = new MessageFuture();
            messageCallbacks.put(id, future);
            attributeValueMap.put(RESPONSE_ID, id);
            attributeValueMap.put(RESPONSE_URL, localUrl);
        }


        TreeMap<String, String> params = new TreeMap<>();
        params.put("Action", "SendMessage");
        params.put("Version", "2012-11-05");

        if (queueUrl.endsWith(".fifo") || queueUrl.endsWith(".fifo/")) {
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

        HttpUtil.get(accessKey, accessSecret, queueUrl, params);

        return future;
    }

    public void deleteMessage(String queueUrl, Message message) throws Exception {
        String request = queueUrl + "?Action=DeleteMessage" +
                "&Version=" + "2012-11-05" +
                "&ReceiptHandle=" + message.getReceiptHandle();
        HttpUtil.get(accessKey, accessSecret, request, null);
    }

    public CompletableFuture<Boolean> consumeQueue(String queueUrl) {
        return consumeQueue(queueUrl, null);
    }

    public CompletableFuture<Boolean> consumeQueue(String queueUrl, Consumer<Message> callback) {
        return consumeQueue(queueUrl, callback, null);
    }

    public CompletableFuture<Boolean> consumeQueue(String queueUrl, Consumer<Message> callback, BiConsumer<Boolean, ? super Throwable> shutdownCallback) {
        CompletableFuture<Boolean> running = new CompletableFuture<>();
        if (shutdownCallback != null) running.whenCompleteAsync(shutdownCallback);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while (!running.isDone()) {
                    read(queueUrl).ifPresent(messageWrappers -> {
                        for (Message message : messageWrappers) {
                            try {
                                String futureId = message.getAttributes().get(FUTURE_ID);
                                if (futureId != null) {
                                    CompletableFuture<Message> completableFuture = messageCallbacks.get(futureId);
                                    if (completableFuture != null) {
                                        completableFuture.complete(message);
                                    }
                                    if (callback != null) callback.accept(message);
                                    deleteMessage(queueUrl, message);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                running.completeExceptionally(e);
            } finally {
                if (!running.isDone()) running.complete(null);
            }
        });
        return running;
    }

    public Optional<List<Message>> read(String queueUrl) throws Exception {
        return read(queueUrl, maxMessages, visibilityTimeout);
    }

    public Optional<List<Message>> read(String queueUrl, int maxMessages, int visibilityTimeout) throws Exception {
        String request = queueUrl + "?Action=ReceiveMessage" +
                "&Version=" + "2012-11-05" +
                "&MaxNumberOfMessages=" + maxMessages +
                "&VisibilityTimeout=" + visibilityTimeout +
                "&WaitTimeSeconds=" + messageTimeout +
                "&AttributeName=" + "All";



        return Optional.ofNullable(MessageParser.parse(HttpUtil.get(accessKey, accessSecret, request, null)));
    }

    public int getMaxMessages() {
        return maxMessages;
    }

    public MessagingClientService setMaxMessages(int maxMessages) {
        this.maxMessages = maxMessages;
        return this;
    }

    public int getMessageTimeout() {
        return messageTimeout;
    }

    public MessagingClientService setMessageTimeout(int messageTimeout) {
        this.messageTimeout = messageTimeout;
        return this;
    }

    public int getVisibilityTimeout() {
        return visibilityTimeout;
    }

    public MessagingClientService setVisibilityTimeout(int visibilityTimeout) {
        this.visibilityTimeout = visibilityTimeout;
        return this;
    }

    public MessagingClientService setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public MessagingClientService setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
        return this;
    }

    public static void main(String[] args) {
        try {
            new MessagingClientService()
                    .setAccessKey("")
                    .setAccessSecret("")
                    .sendMessage("https://sqs.us-east-1.amazonaws.com/604080725100/test.fifo", "Sub mates");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
