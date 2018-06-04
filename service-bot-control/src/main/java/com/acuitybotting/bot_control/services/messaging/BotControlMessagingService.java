package com.acuitybotting.bot_control.services.messaging;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
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
        amazonSQS = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(credentials.getAccessKeyId(), credentials.getSecretKey())))
                .withRegion(region)
                .build();
    }
}
