package com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessageConsumer;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.client.listener.MessagingClientListener;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/10/2018.
 */
@Slf4j
public class RabbitClient implements MessagingClient {

    private String endpoint;
    private String virtualHost;
    private String username;
    private String password;

    private Executor executor = ExecutorUtil.newExecutorPool(1);

    private Gson gson = new Gson();

    private ConnectionFactory factory = new ConnectionFactory();
    private Connection connection;
    private Channel channel;

    private Map<String, MessageFuture> messageFutures = new HashMap<>();
    private List<MessagingClientListener> listeners = new ArrayList<>();
    private Consumer<Throwable> throwableConsumer = Throwable::printStackTrace;
    private Consumer<String> logConsumer = System.out::println;


    @Override
    public Future<Message> send(String targetExchange, String targetRouting, String localQueue, String futureId, String body) throws RuntimeException {
        Channel channel = getChannel();
        if (channel == null || !channel.isOpen()) throw new RuntimeException("Not connected to RabbitMQ.");

        getLog().accept("Sending to exchange '" + targetExchange + "' with routing '" + targetRouting + "' body: " + body);

        Map<String, String> messageAttributes = new HashMap<>();
        String generatedId = null;
        if (futureId != null) messageAttributes.put(FUTURE_ID, futureId);

        MessageFuture future = null;
        if (localQueue != null) {
            generatedId = generateId();
            future = new MessageFuture();
            future.whenComplete((message, throwable) -> messageFutures.remove(futureId));
            messageFutures.put(generatedId, future);
            messageAttributes.put(RESPONSE_ID, generatedId);
            messageAttributes.put(RESPONSE_QUEUE, localQueue);
        }

        Message message = new Message();
        message.setId(generateId());
        message.setAttributes(messageAttributes);
        message.setBody(body);

        for (MessagingClientListener listener : listeners) {
            try {
                listener.beforeMessageSend(message);
            } catch (Throwable e) {
                getExceptionHandler().accept(e);
            }
        }

        try {
            channel.basicPublish(targetExchange, targetRouting, null, getGson().toJson(message).getBytes());
            return future;
        } catch (IOException e) {
            if (generatedId != null) messageFutures.remove(generatedId);
            throw new RuntimeException("Exception during publish.", e);
        }
    }

    @Override
    public void acknowledge(Message message) throws RuntimeException {
        Channel channel = getChannel();
        if (channel == null || !channel.isOpen()) throw new RuntimeException("Not connected to RabbitMQ.");

        try {
            long awkId = Long.parseLong(message.getDeliveryTag());
            channel.basicAck(awkId, false);
            getLog().accept("Acknowledged message with id '" + awkId + "'");
        } catch (IOException e) {
            throw new RuntimeException("Exception during acknowledge.", e);
        }
    }

    public String generateId() {
        return UUID.randomUUID().toString().replaceAll("\\.", "");
    }

    @Override
    public MessageConsumer consume(String queueName, boolean create) {
        return new RabbitConsumer(this, channel, queueName, create);
    }

    @Override
    public void auth(String endpoint, String username, String password) {
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
    }

    @Override
    public void connect() throws RuntimeException {
        factory.setHost(endpoint);
        factory.setUsername(username);
        factory.setPassword(password);
        if (virtualHost != null) factory.setVirtualHost(virtualHost);

        executor.execute(this::open);
    }

    private boolean close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Throwable e) {
                getExceptionHandler().accept(e);
            }
        }

        if (channel != null) {
            try {
                channel.close();
            } catch (Throwable e) {
                getExceptionHandler().accept(e);
            }
        }

        if (connection != null && !connection.isOpen()) channel = null;
        if (channel != null && !channel.isOpen()) channel = null;

        return connection == null && channel == null;
    }

    private void open() {
        try {
            if (close()) {
                connection = factory.newConnection();
                channel = connection.createChannel();
                if (connection.isOpen() && channel.isOpen()) {
                    getLog().accept("RabbitMq connection and channel opened.");
                    for (MessagingClientListener listener : listeners) {
                        try {
                            listener.onConnect();
                        } catch (Throwable e) {
                            getExceptionHandler().accept(e);
                        }
                    }
                    return;
                }
            }
        } catch (Throwable e) {
            getExceptionHandler().accept(e);
        }

        getLog().accept("Failed to open RabbitMQ connection, waiting 10 seconds and trying again.");

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(10));
        } catch (InterruptedException e) {
            getExceptionHandler().accept(e);
        }

        executor.execute(this::open);
    }

    public RabbitClient setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
        return this;
    }

    @Override
    public MessageFuture getMessageFuture(String id) {
        return messageFutures.get(id);
    }

    @Override
    public List<MessagingClientListener> getListeners() {
        return listeners;
    }

    @Override
    public Consumer<Throwable> getExceptionHandler() {
        return throwableConsumer;
    }

    @Override
    public boolean isConnected() {
        return channel != null && channel.isOpen();
    }

    public Channel getChannel() {
        return channel;
    }

    public Gson getGson() {
        return gson;
    }

    public Consumer<String> getLog() {
        return logConsumer;
    }
}
