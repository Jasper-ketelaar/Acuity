package com.acuitybotting.data.flow.messaging.services;

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


    private Map<String, CompletableFuture<MessageWrapper>> messageCallbacks = new HashMap<>();

    private int maxMessages = 3;
    private int messageTimeout = 20;

    public CompletableFuture<MessageWrapper> sendMessage(String queueUrl, String localUrl, String body){
        return sendMessage(queueUrl, localUrl, null, body);
    }

    public void sendMessage(String queueUrl, String body){
        sendMessage(queueUrl, null, null, body);
    }

    public void respondToMessage(MessageWrapper message, String body) {
        respondToMessage(message, null, body);
    }

    public CompletableFuture<MessageWrapper> respondToMessage(MessageWrapper message, String localUrl, String body) {
        String responseId = message.getAttributes().get(RESPONSE_ID);
        String responseUrl = message.getAttributes().get(RESPONSE_URL);
        return sendMessage(responseUrl, localUrl, responseId, body);
    }

    private CompletableFuture<MessageWrapper> sendMessage(String queueUrl, String localUrl, String futureId, String body){
        Map<String, String> attributeValueMap = new HashMap<>();

        if (futureId != null){
            attributeValueMap.put(FUTURE_ID, futureId);
        }

        CompletableFuture<MessageWrapper> future = null;
        if (localUrl != null){
            String id = UUID.randomUUID().toString().replaceAll("\\.", "-");
            future = new CompletableFuture<>();
            messageCallbacks.put(id, future);
            attributeValueMap.put(RESPONSE_ID, id);
            attributeValueMap.put(RESPONSE_URL, localUrl);
        }

        //Todo make http request.
        return future;
    }

    public void deleteMessage(String queueUrl, MessageWrapper message){
        //Todo make http request.
    }

    public CompletableFuture<Boolean> consumeQueue(String queueUrl){
        return consumeQueue(queueUrl, null);
    }

    public CompletableFuture<Boolean> consumeQueue(String queueUrl, Consumer<MessageWrapper> callback){
        return consumeQueue(queueUrl, callback, null);
    }

    public CompletableFuture<Boolean> consumeQueue(String queueUrl, Consumer<MessageWrapper> callback, BiConsumer<Boolean, ? super Throwable> shutdownCallback){
        CompletableFuture<Boolean> running = new CompletableFuture<>();
        if (shutdownCallback != null) running.whenCompleteAsync(shutdownCallback);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while (!running.isDone()){
                    read(queueUrl).ifPresent(messageWrappers -> {
                        for (MessageWrapper messageWrapper : messageWrappers) {
                            try {
                                String futureId = messageWrapper.getAttributes().get(FUTURE_ID);
                                if (futureId != null){
                                    CompletableFuture<MessageWrapper> completableFuture = messageCallbacks.get(futureId);
                                    if (completableFuture != null){
                                        completableFuture.complete(messageWrapper);
                                    }
                                    if (callback != null) callback.accept(messageWrapper);
                                    deleteMessage(queueUrl, messageWrapper);
                                }
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

    public Optional<List<MessageWrapper>> read(String queueUrl){
        return read(queueUrl, maxMessages, messageTimeout);
    }

    public Optional<List<MessageWrapper>> read(String queueUrl, int maxMessages, int timeout){
        return null; //Todo make http request.
    }
}
