package com.acuitybotting.data.flow.messaging.services.interfaces;

import com.acuitybotting.data.flow.messaging.services.Message;

/**
 * Created by Zachary Herridge on 7/6/2018.
 */
public interface MessagingClientListener {

    void onMessageSend(Message message);

}
