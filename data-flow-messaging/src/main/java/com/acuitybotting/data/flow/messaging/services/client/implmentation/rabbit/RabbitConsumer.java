package com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessageConsumer;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/10/2018.
 */
public class RabbitConsumer extends DefaultConsumer implements MessageConsumer {

    private RabbitClient rabbitClient;
    private String queue;
    private List<BiConsumer<MessageConsumer, Message>> consumers = new ArrayList<>();
    private String consumeId;
    private boolean autoAcknowledge = true;

    public RabbitConsumer(RabbitClient rabbitClient, Channel channel, String queue) {
        super(channel);
        this.rabbitClient = rabbitClient;
        this.queue = queue;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            Message message = rabbitClient.getGson().fromJson(new String(body), Message.class);
            message.setDeliveryTag(String.valueOf(envelope.getDeliveryTag()));

            if (message.getAttributes() != null){
                String futureId = message.getAttributes().get(MessagingClient.FUTURE_ID);
                if (futureId != null) {
                    MessageFuture messageFuture = rabbitClient.getMessageFuture(futureId);
                    if (messageFuture != null) {
                        messageFuture.complete(message);
                    }
                }
            }

            for (BiConsumer<MessageConsumer, Message> messageCallback : getMessageCallbacks()) {
                try {
                    messageCallback.accept(this, message);
                } catch (Exception e) {
                    rabbitClient.getExceptionHandler().accept(e);
                }
            }
        }
        catch (Throwable e){
            rabbitClient.getExceptionHandler().accept(e);
        }
    }

    @Override
    public List<BiConsumer<MessageConsumer, Message>> getMessageCallbacks() {
        return consumers;
    }

    @Override
    public MessageConsumer bind(String exchange, String routing) throws RuntimeException {
        Channel channel = rabbitClient.getChannel();
        if (channel == null || !channel.isOpen()) throw new RuntimeException("Not connected to RabbitMQ.");

        try {
            channel.queueBind(queue, exchange, routing);
            rabbitClient.getLog().accept("Bound queue '" + queue + "' to exchange '" + exchange + "' with routing '" + routing + "'.");
        } catch (IOException e) {
            throw new RuntimeException("Exception during binding.", e);
        }

        return this;
    }

    @Override
    public MessageConsumer start() throws RuntimeException {
        Channel channel = rabbitClient.getChannel();
        if (channel == null || !channel.isOpen()) throw new RuntimeException("Not connected to RabbitMQ.");

        if (queue == null){
            try {
                queue = channel.queueDeclare(rabbitClient.generateId(), false, true, true, null).getQueue();
                rabbitClient.getLog().accept("Declared queue '" + queue + "'.");
            } catch (IOException e) {
                throw new RuntimeException("Exception during queue creation.", e);
            }
        }

        try {
            consumeId = channel.basicConsume(queue, autoAcknowledge,this);
            rabbitClient.getLog().accept("Consuming queue '" + queue + "' with consumeId '" + consumeId + "'.");
        } catch (IOException e) {
            throw new RuntimeException("Exception during queue consume.", e);
        }

        return this;
    }

    @Override
    public MessageConsumer cancel() throws RuntimeException {
        Channel channel = rabbitClient.getChannel();
        if (channel != null && channel.isOpen()){
            try {
                channel.basicCancel(consumeId);
                rabbitClient.getLog().accept("Canceled consume '" + consumeId + "'.");
            } catch (IOException e) {
                throw new RuntimeException("Exception during queue cancel.", e);
            }
        }
        return this;
    }

    @Override
    public MessageConsumer withAutoAcknowledge(boolean autoAcknowledge) {
        this.autoAcknowledge = autoAcknowledge;
        return this;
    }

    @Override
    public String getQueue() {
        return queue;
    }

    @Override
    public MessagingClient getClient() {
        return rabbitClient;
    }
}
