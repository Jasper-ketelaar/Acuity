package com.acuitybotting.data.flow.messaging.services.client.message;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Zachary Herridge on 6/19/2018.
 */
public class MessageFuture extends CompletableFuture<Message> {

    private long creationTime = System.currentTimeMillis();

}
