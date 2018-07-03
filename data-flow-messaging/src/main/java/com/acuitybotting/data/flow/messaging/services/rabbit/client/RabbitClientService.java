package com.acuitybotting.data.flow.messaging.services.rabbit.client;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.futures.EmptyMessageFuture;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.acuitybotting.data.flow.messaging.services.interfaces.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.sqs.client.SqsMessageConsumer;
import com.google.gson.Gson;
import com.rabbitmq.client.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/2/2018.
 */
@Slf4j
@Getter
public class RabbitClientService implements MessagingClient {

    private EmptyMessageFuture EMPTY_MESSAGE_FUTURE = new EmptyMessageFuture();

    private Consumer<Throwable> exceptionHandler = throwable -> log.error("Error in Rabbit", throwable);
    private ExecutorService executorService = ExecutorUtil.newExecutorPool(1, exceptionHandler);

    private Gson gson = new Gson();

    private List<Consumer<Message>> appenders = new ArrayList<>();
    private Map<String, MessageFuture> messageCallbacks = new HashMap<>();

    private ConnectionFactory connectionFactory = new ConnectionFactory();
    private Connection connection;
    private Channel channel;

    @Override
    public void start(String vHost, String host, int port, String username, String password){
        EMPTY_MESSAGE_FUTURE.cancel(true);

        connectionFactory.setHost(host);
        connectionFactory.setPort(port);

        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        connectionFactory.setVirtualHost(vHost);

        executorService.execute(this::connect);
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
    public Optional<MessageFuture> send(String targetExchange, String targetQueue, String localQueue, String futureId, String body) {
        Message message = new Message();

        Map<String, String> attributes = new HashMap<>();

        if (futureId != null) attributes.put(FUTURE_ID, futureId);

        MessageFuture future = EMPTY_MESSAGE_FUTURE;
        if (localQueue != null){
            String callbackId = getUUID();
            future = new MessageFuture();
            messageCallbacks.put(callbackId, future);
            attributes.put(RESPONSE_ID, callbackId);
            attributes.put(RESPONSE_QUEUE, localQueue);
        }

        message.setAttributes(attributes);
        message.setBody(body);
        message.setId(getUUID());

        for (Consumer<Message> messageConsumer : getMessageAppenders()) {
            messageConsumer.accept(message);
        }

        try {
            channel.basicPublish(targetExchange, targetQueue, null, gson.toJson(message).getBytes());
            return Optional.of(future);
        }
        catch (IOException e) {
            exceptionHandler.accept(e);
        }

        return Optional.empty();
    }

    @Override
    public SqsMessageConsumer consume(String queue, Consumer<Message> callback) {
        try {
            RabbitMessageConsumer rabbitMessageConsumer = new RabbitMessageConsumer(queue, this, channel);
            if (callback != null) rabbitMessageConsumer.getMessageCallbacks().add(callback);
            channel.basicConsume(queue, rabbitMessageConsumer);
        } catch (IOException e) {
            exceptionHandler.accept(e);
        }

        return null;
    }

    @Override
    public boolean delete(Message message) {
        try {
            channel.basicAck(Long.parseLong(message.getDeliveryTag()), false);
            return true;
        }
        catch (IOException e) {
            exceptionHandler.accept(e);
        }
        return false;
    }

    public String getUUID(){
        return UUID.randomUUID().toString().replaceAll("\\.", "-");
    }

    @Override
    public Consumer<Throwable> getExceptionHandler() {
        return exceptionHandler;
    }

    public Map<String, MessageFuture> getMessageCallbacks() {
        return messageCallbacks;
    }

    public void onConnect(Connection connection, Channel channel){
    }

    private void connect(){
        if (channel != null) return;

        try {
            connection = connectionFactory.newConnection();
            if (connection != null){
                channel = connection.createChannel();
                if (channel != null) {
                    onConnect(connection, channel);
                }
            }
        } catch (Throwable e) {
            exceptionHandler.accept(e);
        }

        executorService.execute(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                exceptionHandler.accept(e);
            }
            this.connect();
        });
    }
}
