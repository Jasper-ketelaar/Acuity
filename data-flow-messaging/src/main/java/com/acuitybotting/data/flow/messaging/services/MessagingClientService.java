package com.acuitybotting.data.flow.messaging.services;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 6/7/2018.
 */
@Service
public class MessagingClientService {

    private static final String FUTURE_ID = "futureId";

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

    public CompletableFuture<Message> sendCompleteableMessage(String queueUrl, String body){
        String id = UUID.randomUUID().toString().replaceAll("\\.", "-");
        CompletableFuture<Message> completableFuture = new CompletableFuture<>();
        messageCallbacks.put(id, completableFuture);
        getSQS().sendMessage(new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(body)
                .withMessageAttributes(Collections.singletonMap(FUTURE_ID, new MessageAttributeValue().withStringValue(id)))
        );
        return completableFuture;
    }

    public void sendMessage(String queueUrl, String body){
        getSQS().sendMessage(new SendMessageRequest().withQueueUrl(queueUrl).withMessageBody(body));
    }

    public void deleteMessage(String queueUrl, Message message){
        getSQS().deleteMessage(new DeleteMessageRequest().withQueueUrl(queueUrl).withReceiptHandle(message.getReceiptHandle()));
    }

    public CompletableFuture<Void> consumeQueue(String queueUrl){
        return consumeQueue(queueUrl, message -> {});
    }

    public CompletableFuture<Void> consumeQueue(String queueUrl, Consumer<Message> callback){
        CompletableFuture<Void> running = new CompletableFuture<>();
        Executors.newSingleThreadExecutor().submit(() -> {
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
                            callback.accept(message);
                            deleteMessage(queueUrl, message);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
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
        return Optional.ofNullable(getSQS().receiveMessage(receiveMessageRequest));
    }
}
