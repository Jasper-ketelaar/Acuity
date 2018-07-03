package com.acuitybotting.data.flow.messaging.services.rabbit.client;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.acuitybotting.data.flow.messaging.services.interfaces.MessageConsumer;
import com.acuitybotting.data.flow.messaging.services.interfaces.MessagingClient;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/2/2018.
 */
public class RabbitMessageConsumer extends DefaultConsumer implements MessageConsumer {

    private static final Gson gson = new Gson();

    private final String queue;
    private final RabbitClientService messagingClient;
    private final List<Consumer<Message>> messageCallbacks = new ArrayList<>();

    public RabbitMessageConsumer(String queue, RabbitClientService messagingClient, Channel channel) {
        super(channel);
        this.queue = queue;
        this.messagingClient = messagingClient;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String bodyJson = new String(body);

        Message message = gson.fromJson(bodyJson, Message.class);
        message.setSource(queue);
        message.setDeliveryTag(String.valueOf(envelope.getDeliveryTag()));

        String futureId = message.getAttributes().get(MessagingClient.FUTURE_ID);
        if (futureId != null){
            MessageFuture messageFuture = messagingClient.getMessageFuture(futureId);
            if (messageFuture != null) messageFuture.complete(message);
        }

        for (Consumer<Message> messageConsumer : getMessageCallbacks()) {
            messageConsumer.accept(message);
        }
    }

    @Override
    public List<Consumer<Message>> getMessageCallbacks() {
        return messageCallbacks;
    }

    @Override
    public MessageConsumer start() {
        try {
            messagingClient.getChannel().basicConsume(queue, this);
        } catch (IOException e) {
            messagingClient.getExceptionHandler().accept(e);
        }
        return this;
    }

    @Override
    public MessageConsumer cancel() {
        try {
            getChannel().basicCancel(getConsumerTag());
        } catch (IOException e) {
            messagingClient.getExceptionHandler().accept(e);
        }
        return this;
    }
}
