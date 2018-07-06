package com.acuitybotting.data.flow.messaging.services.aws.iot;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.acuitybotting.data.flow.messaging.services.interfaces.MessageConsumer;
import com.acuitybotting.data.flow.messaging.services.interfaces.MessagingClient;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotTopic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/6/2018.
 */
public class IotMessageConsumer extends AWSIotTopic implements MessageConsumer {

    private IotClientService iotClientService;
    private List<Consumer<Message>> messageCallbacks = new ArrayList<>();

    public IotMessageConsumer(IotClientService iotClientService, String topic) {
        super(topic);
        this.iotClientService = iotClientService;
    }

    @Override
    public List<Consumer<Message>> getMessageCallbacks() {
        return messageCallbacks;
    }

    @Override
    public MessageConsumer start() throws AWSIotException {
        iotClientService.getClient().subscribe(this);
        return this;
    }

    @Override
    public MessageConsumer cancel() throws AWSIotException {
        iotClientService.getClient().unsubscribe(this);
        return this;
    }

    @Override
    public void onMessage(AWSIotMessage awsIotMessage) {
        try {
            String stringPayload = awsIotMessage.getStringPayload();

            Message message = iotClientService.getGson().fromJson(stringPayload, Message.class);
            message.setSource(awsIotMessage.getTopic());

            if (message.getAttributes() != null){
                String futureId = message.getAttributes().get(MessagingClient.FUTURE_ID);
                if (futureId != null) {
                    MessageFuture messageFuture = iotClientService.getMessageFuture(futureId);
                    if (messageFuture != null) {
                        messageFuture.complete(message);
                    }
                }
            }

            for (Consumer<Message> messageCallback : messageCallbacks) {
                try {
                    messageCallback.accept(message);
                } catch (Exception e) {
                    iotClientService.getExceptionHandler().accept(e);
                }
            }
        } catch (Exception e){
            iotClientService.getExceptionHandler().accept(e);
        }
    }
}
