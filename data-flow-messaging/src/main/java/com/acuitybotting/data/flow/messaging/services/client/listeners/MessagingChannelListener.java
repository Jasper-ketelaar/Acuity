package com.acuitybotting.data.flow.messaging.services.client.listeners;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;

/**
 * Created by Zachary Herridge on 7/16/2018.
 */
public interface MessagingChannelListener {

    void onConnect(MessagingChannel channel);

    void beforeMessageSend(MessagingChannel channel, Message message);

    void onMessage(MessageEvent messageEvent);

    void onShutdown(MessagingChannel channel, Throwable cause);
}
