package com.acuitybotting.data.flow.messaging.services.sqs.client;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.futures.MessageFuture;
import com.acuitybotting.data.flow.messaging.services.interfaces.MessageConsumer;
import com.acuitybotting.data.flow.messaging.services.interfaces.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.sqs.client.util.HttpUtil;
import com.acuitybotting.data.flow.messaging.services.sqs.client.util.MessageParser;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Created by Zachary Herridge on 7/3/2018.
 */
public class SqsMessageConsumer implements MessageConsumer {

    private SqsClientService sqsClientService;
    private String queue;
    private List<Consumer<Message>> messageCallbacks = new ArrayList<>();

    private boolean deleteMessageOnConsume = true;
    private int maxMessages = 3;
    private int messageTimeout = 20;
    private int visibilityTimeout = 20;

    private ExecutorService executorService = ExecutorUtil.newExecutorPool(1);

    public SqsMessageConsumer(SqsClientService sqsClientService, String queue) {
        this.sqsClientService = sqsClientService;
        this.queue = queue;
        executorService.submit(this::execute);
    }

    private void execute(){
        try {
            List<Message> messages = read().orElse(Collections.emptyList());

            for (Message message : messages) {
                String futureId = message.getAttributes().get(MessagingClient.FUTURE_ID);
                if (futureId != null){
                    MessageFuture messageFuture = sqsClientService.getMessageFuture(futureId);
                    if (messageFuture != null) messageFuture.complete(message);
                }

                for (Consumer<Message> messageCallback : messageCallbacks) {
                    try {
                        messageCallback.accept(message);
                        if (deleteMessageOnConsume) sqsClientService.delete(message);
                    }
                    catch (Exception e){
                        sqsClientService.getExceptionHandler().accept(e);
                    }
                }
            }
        } catch (Exception e) {
            sqsClientService.getExceptionHandler().accept(e);
        }

        executorService.submit(this::execute);
    }

    @Override
    public List<Consumer<Message>> getMessageCallbacks() {
        return messageCallbacks;
    }

    @Override
    public void cancel() {
        executorService.shutdownNow();
    }

    private Optional<List<Message>> read() throws Exception {
        String request = queue + "?Action=ReceiveMessage" +
                "&Version=" + "2012-11-05" +
                "&MaxNumberOfMessages=" + maxMessages +
                "&VisibilityTimeout=" + visibilityTimeout +
                "&WaitTimeSeconds=" + messageTimeout +
                "&AttributeName=" + "All";

        return Optional.ofNullable(MessageParser.parse(HttpUtil.get(sqsClientService.getHeaders(), request, null)));
    }
}
