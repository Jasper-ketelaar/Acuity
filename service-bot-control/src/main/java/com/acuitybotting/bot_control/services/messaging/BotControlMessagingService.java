package com.acuitybotting.bot_control.services.messaging;

import com.acuitybotting.aws.security.AwsSecretService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
public class BotControlMessagingService {

    private final AwsSecretService secretService;
    private final AmazonSQS amazonSQS;

    @Autowired
    public BotControlMessagingService(AwsSecretService secretService) {
        this.secretService = secretService;
        this.amazonSQS = amazonSQSClient();
    }

    public AmazonSQS getSQS() {
        return amazonSQS;
    }

    private AmazonSQS amazonSQSClient() {
        Credentials sqsAccess = new Gson().fromJson(secretService.getSecret("secretsmanager.us-east-1.amazonaws.com", "us-east-1", "SqsAccess").orElseThrow(() -> new RuntimeException("Failed to get SQS secrete.")), Credentials.class);
        return AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(sqsAccess.getAccessKey(), sqsAccess.getSecretKey())))
                .withRegion(sqsAccess.getRegion())
                .build();
    }
}
