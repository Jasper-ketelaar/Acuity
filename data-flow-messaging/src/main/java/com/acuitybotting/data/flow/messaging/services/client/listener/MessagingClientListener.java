package com.acuitybotting.data.flow.messaging.services.client.listener;

import com.acuitybotting.data.flow.messaging.services.Message;

/**
 * Created by Zachary Herridge on 7/6/2018.
 */
public interface MessagingClientListener {

    void onConnect();

    void beforeMessageSend(Message message);

    void onDisconnect();

    void onShutdown();

}
