package com.acuitybotting.data.flow.messaging.services.client;

import com.acuitybotting.data.flow.messaging.services.Message;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/2/2018.
 */
public interface MessageConsumer {

    List<BiConsumer<MessageConsumer, Message>> getMessageCallbacks();

    default MessageConsumer withCallback(BiConsumer<MessageConsumer, Message> callback) {
        getMessageCallbacks().add(callback);
        return this;
    }

    MessageConsumer withAutoAcknowledge(boolean autoAcknowledge);

    MessageConsumer bind(String exchange, String routing) throws RuntimeException;

    MessageConsumer start() throws RuntimeException;

    MessageConsumer cancel() throws RuntimeException;

    String getQueue();

    MessagingClient getClient();

    default void acknowledge(Message message) throws RuntimeException {
        getClient().acknowledge(message);
    }
}
