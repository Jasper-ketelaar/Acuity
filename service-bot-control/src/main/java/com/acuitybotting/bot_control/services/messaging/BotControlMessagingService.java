package com.acuitybotting.bot_control.services.messaging;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import org.springframework.stereotype.Service;
import com.amazonaws.services.cognitoidentity.model.Credentials;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
public class BotControlMessagingService {

    private AmazonSQS amazonSQS;

    public AmazonSQS getSQS() {
        return amazonSQS;
    }

    public void connect(String region, Credentials credentials) {
        BasicSessionCredentials awsCreds = new BasicSessionCredentials(credentials.getAccessKeyId(), credentials.getSecretKey(), credentials.getSessionToken());
        amazonSQS = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(region)
                .build();
    }
}
