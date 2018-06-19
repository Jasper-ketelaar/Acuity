package com.acuitybotting.bot_control.services.messaging;

import com.acuitybotting.data.flow.messaging.services.client.MessagingClientService;
import com.acuitybotting.data.flow.messaging.services.MessagingQueueService;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
public class BotControlMessagingService {

    private final MessagingQueueService queueService;
    private final MessagingClientService clientService;

    @Autowired
    public BotControlMessagingService(MessagingQueueService queueService, MessagingClientService clientService) {
        this.queueService = queueService;
        this.clientService = clientService;
    }

    public MessagingClientService getClientService() {
        return clientService;
    }

    public MessagingQueueService getQueueService() {
        return queueService;
    }

    public void connect(String region, Credentials credentials) {
        AWSCredentials awsCredentials;
        if (credentials.getSessionToken() != null){
            awsCredentials = new BasicSessionCredentials(credentials.getAccessKeyId(), credentials.getSecretKey(), credentials.getSessionToken());
        }
        else {
            awsCredentials = new BasicAWSCredentials(credentials.getAccessKeyId(), credentials.getSecretKey());
        }
        AmazonSQS build = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region)
                .build();
        queueService.setAmazonSQS(build);
    }
}
