package com.acuitybotting.data.flow.messaging.services.events;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
public class MessageEvent {

    private MessagingChannel channel;
    private Message message;

    public MessagingChannel getChannel() {
        return channel;
    }

    public MessageEvent setChannel(MessagingChannel channel) {
        this.channel = channel;
        return this;
    }

    public Message getMessage() {
        return message;
    }

    public MessageEvent setMessage(Message message) {
        this.message = message;
        return this;
    }

    public String getRouting(){
        return String.valueOf(message.getAttributes().getOrDefault("envelope.routing", ""));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageEvent{");
        sb.append("channel=").append(channel);
        sb.append(", message=").append(message);
        sb.append('}');
        return sb.toString();
    }
}
