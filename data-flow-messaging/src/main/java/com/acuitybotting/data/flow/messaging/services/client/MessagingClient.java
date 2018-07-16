package com.acuitybotting.data.flow.messaging.services.client;

import com.acuitybotting.data.flow.messaging.services.client.listeners.MessagingClientListener;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/2/2018.
 */
public interface MessagingClient {

    String FUTURE_ID = "futureId";
    String RESPONSE_ID = "responseId";
    String RESPONSE_QUEUE = "responseQueue";

    void auth(String endpoint, String username, String password);

    void connect() throws RuntimeException;

    List<MessagingClientListener> getListeners();

    Consumer<Throwable> getExceptionHandler();

    boolean isConnected();

    MessagingChannel createChannel();
}
