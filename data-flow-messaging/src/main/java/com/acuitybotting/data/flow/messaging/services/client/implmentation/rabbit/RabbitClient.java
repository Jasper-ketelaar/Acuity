package com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.client.listeners.MessagingClientListener;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.google.gson.Gson;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
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

    private Executor executor = ExecutorUtil.newExecutorPool(3);

    private Gson gson = new Gson();

    private ConnectionFactory factory = new ConnectionFactory();
    private Connection connection;

    private Map<String, MessageFuture> messageFutures = new HashMap<>();
    private List<MessagingClientListener> listeners = new CopyOnWriteArrayList<>();

    private Consumer<Throwable> throwableConsumer = throwable -> log.error("Error from Rabbit.", throwable);
    private Consumer<String> logConsumer = s -> log.info(s);

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

        executor.execute(this::doConnect);
    }

    private boolean close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Throwable e) {
                getExceptionHandler().accept(e);
            }
        }

        if (connection != null && !connection.isOpen()) connection = null;
        return connection == null;
    }

    private void doConnect() {
        try {
            if (close()) {
                connection = factory.newConnection();
                if (connection.isOpen()) {
                    getLog().accept("RabbitMq connection opened.");
                    for (MessagingClientListener listener : listeners) {
                        try {
                            listener.onConnect(this);
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

        executor.execute(this::doConnect);
    }

    public RabbitClient setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
        return this;
    }

    public Executor getExecutor() {
        return executor;
    }

    public Map<String, MessageFuture> getMessageFutures() {
        return messageFutures;
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
        return connection != null || connection.isOpen();
    }

    @Override
    public MessagingChannel createChannel() throws RuntimeException {
        return new RabbitChannel(this);
    }

    public Gson getGson() {
        return gson;
    }

    public Consumer<String> getLog() {
        return logConsumer;
    }

    public Connection getConnection() {
        return connection;
    }
}
