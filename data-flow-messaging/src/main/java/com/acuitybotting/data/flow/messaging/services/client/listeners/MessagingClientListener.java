package com.acuitybotting.data.flow.messaging.services.client.listeners;

import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;

/**
 * Created by Zachary Herridge on 7/6/2018.
 */
public interface MessagingClientListener {

    void onConnect(MessagingClient client);

    void onShutdown(MessagingClient client);
}
