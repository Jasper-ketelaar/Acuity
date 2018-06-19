package com.acuitybotting.data.flow.messaging.services.client;

import java.util.Map;

/**
 * Created by Zachary Herridge on 6/19/2018.
 */
public class MessageWrapper {

    private String id;
    private String receiptHandle;
    private String bodyMD5;
    private String body;
    private Map<String, String> attributes;

    public String getId() {
        return id;
    }

    public MessageWrapper setId(String id) {
        this.id = id;
        return this;
    }

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public MessageWrapper setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
        return this;
    }

    public String getBodyMD5() {
        return bodyMD5;
    }

    public MessageWrapper setBodyMD5(String bodyMD5) {
        this.bodyMD5 = bodyMD5;
        return this;
    }

    public String getBody() {
        return body;
    }

    public MessageWrapper setBody(String body) {
        this.body = body;
        return this;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public MessageWrapper setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageWrapper{");
        sb.append("id='").append(id).append('\'');
        sb.append(", receiptHandle='").append(receiptHandle).append('\'');
        sb.append(", bodyMD5='").append(bodyMD5).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }
}
