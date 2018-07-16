package com.acuitybotting.data.flow.messaging.services.client.listeners.adapters;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.listeners.MessagingChannelListener;

/**
 * Created by Zachary Herridge on 7/16/2018.
 */
public class MessagingChannelAdapter implements MessagingChannelListener {

    @Override
    public void onConnect(MessagingChannel channel) {

    }

    @Override
    public void beforeMessageSend(MessagingChannel channel, Message message) {

    }

    @Override
    public void onMessage(MessagingChannel channel, Message message) {

    }

    @Override
    public void onShutdown(MessagingChannel channel, Throwable cause) {

    }
}
