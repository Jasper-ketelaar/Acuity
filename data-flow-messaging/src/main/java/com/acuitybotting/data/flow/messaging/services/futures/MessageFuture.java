package com.acuitybotting.data.flow.messaging.services.futures;


import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Zachary Herridge on 6/19/2018.
 */
public class MessageFuture extends CompletableFuture<MessageEvent> {

    private long creationTime = System.currentTimeMillis();

    public long getCreationTime() {
        return creationTime;
    }
}
