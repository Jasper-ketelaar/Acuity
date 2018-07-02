package com.acuitybotting.data.flow.messaging.services;

import java.util.Map;

/**
 * Created by Zachary Herridge on 6/19/2018.
 */
public class Message {

    private String id;
    private String deliveryTag;
    private String body;
    private Map<String, String> attributes;

    public String getId() {
        return id;
    }

    public Message setId(String id) {
        this.id = id;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Message setBody(String body) {
        this.body = body;
        return this;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public Message setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    public String getDeliveryTag() {
        return deliveryTag;
    }

    public Message setDeliveryTag(String deliveryTag) {
        this.deliveryTag = deliveryTag;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("id='").append(id).append('\'');
        sb.append(", deliveryTag='").append(deliveryTag).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }
}
