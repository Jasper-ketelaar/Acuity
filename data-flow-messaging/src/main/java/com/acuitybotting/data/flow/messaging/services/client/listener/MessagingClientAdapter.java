package com.acuitybotting.data.flow.messaging.services.client.listener;

import com.acuitybotting.data.flow.messaging.services.Message;

/**
 * Created by Zachary Herridge on 7/10/2018.
 */
public class MessagingClientAdapter implements MessagingClientListener {

    @Override
    public void onConnect() {
    }

    @Override
    public void beforeMessageSend(Message message) {
    }

    @Override
    public void onDisconnect() {
    }

    @Override
    public void onShutdown() {
    }
}
