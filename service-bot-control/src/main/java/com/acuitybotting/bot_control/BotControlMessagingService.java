package com.acuitybotting.bot_control;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
public class BotControlMessagingService {

    private final AmazonSQS amazonSQS;

    @Autowired
    public BotControlMessagingService(AmazonSQS amazonSQS) {
        this.amazonSQS = amazonSQS;
    }

    public AmazonSQS getSQS() {
        return amazonSQS;
    }

    @Bean(name = "amazonSQS", destroyMethod = "shutdown")
    public AmazonSQS amazonSQSClient() {
        return AmazonSQSClientBuilder.defaultClient();
    }
}
