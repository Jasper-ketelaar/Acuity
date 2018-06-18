package com.acuitybotting.data.flow.messaging.services;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import lombok.Getter;
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

    private AmazonSQS amazonSQS;

    private Map<String, CompletableFuture<Message>> messageCallbacks = new HashMap<>();

    private int maxMessages = 3;
    private int messageTimeout = 20;

    public AmazonSQS getSQS() {
        return Optional.ofNullable(amazonSQS).orElseThrow(() -> new RuntimeException("Connect to SQS before referencing the SQS object. Call messagingClientService.setAmazonSQS()."));
    }

    public MessagingClientService setAmazonSQS(AmazonSQS amazonSQS) {
        this.amazonSQS = amazonSQS;
        return this;
    }

    public CompletableFuture<Message> sendMessage(String queueUrl, String localUrl, String body){
        return sendMessage(queueUrl, localUrl, null, body);
    }

    public void sendMessage(String queueUrl, String body){
        sendMessage(queueUrl, null, null, body);
    }

    public void respondToMessage(Message message, String body) {
        respondToMessage(message, null, body);
    }

    public CompletableFuture<Message> respondToMessage(Message message, String localUrl, String body) {
        String responseId = message.getMessageAttributes().get(RESPONSE_ID).getStringValue();
        String responseUrl = message.getMessageAttributes().get(RESPONSE_URL).getStringValue();
        return sendMessage(responseUrl, localUrl, responseId, body);
    }

    private CompletableFuture<Message> sendMessage(String queueUrl, String localUrl, String futureId, String body){
        Map<String, MessageAttributeValue> attributeValueMap = new HashMap<>();

        if (futureId != null){
            attributeValueMap.put(FUTURE_ID, new MessageAttributeValue().withDataType("String").withStringValue(futureId));
        }

        CompletableFuture<Message> future = null;
        if (localUrl != null){
            String id = UUID.randomUUID().toString().replaceAll("\\.", "-");
            future = new CompletableFuture<>();
            messageCallbacks.put(id, future);
            attributeValueMap.put(RESPONSE_ID, new MessageAttributeValue().withDataType("String").withStringValue(id));
            attributeValueMap.put(RESPONSE_URL, new MessageAttributeValue().withDataType("String").withStringValue(localUrl));
        }

        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withMessageGroupId("channel-1")
                .withMessageDeduplicationId(UUID.randomUUID().toString())
                .withQueueUrl(queueUrl)
                .withMessageBody(body)
                .withMessageAttributes(attributeValueMap);

        getSQS().sendMessage(sendMessageRequest);
        return future;
    }

    public void deleteMessage(String queueUrl, Message message){
        getSQS().deleteMessage(new DeleteMessageRequest().withQueueUrl(queueUrl).withReceiptHandle(message.getReceiptHandle()));
    }

    public CompletableFuture<Boolean> consumeQueue(String queueUrl){
        return consumeQueue(queueUrl, null);
    }

    public CompletableFuture<Boolean> consumeQueue(String queueUrl, Consumer<Message> callback){
        return consumeQueue(queueUrl, callback, null);
    }

    public CompletableFuture<Boolean> consumeQueue(String queueUrl, Consumer<Message> callback, BiConsumer<Boolean, ? super Throwable> shutdownCallback){
        CompletableFuture<Boolean> running = new CompletableFuture<>();
        if (shutdownCallback != null) running.whenCompleteAsync(shutdownCallback);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while (!running.isDone()){
                    read(queueUrl).ifPresent(receiveMessageResult -> {
                        for (Message message : receiveMessageResult.getMessages()) {
                            try {
                                MessageAttributeValue messageAttributeValue = message.getMessageAttributes().get(FUTURE_ID);
                                if (messageAttributeValue != null){
                                    CompletableFuture<Message> completableFuture = messageCallbacks.get(messageAttributeValue.getStringValue());
                                    if (completableFuture != null){
                                        completableFuture.complete(message);
                                    }
                                }
                                if (callback != null) callback.accept(message);
                                deleteMessage(queueUrl, message);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            catch (Exception e){
                running.completeExceptionally(e);
            }
            finally {
                if (!running.isDone()) running.complete(null);
            }
        });
        return running;
    }

    public Optional<ReceiveMessageResult> read(String queueUrl){
        return read(queueUrl, maxMessages, messageTimeout);
    }

    public Optional<ReceiveMessageResult> read(String queueUrl, int maxMessages, int timeout){
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest();
        receiveMessageRequest.withQueueUrl(queueUrl);
        receiveMessageRequest.withMaxNumberOfMessages(maxMessages);
        receiveMessageRequest.withWaitTimeSeconds(timeout);
        receiveMessageRequest.withMessageAttributeNames("All");
        return Optional.ofNullable(getSQS().receiveMessage(receiveMessageRequest));
    }
}
