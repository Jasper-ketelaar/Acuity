package com.acuitybotting.bot_control.services.messaging;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.SQSActions;
import com.amazonaws.auth.policy.conditions.StringCondition;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import com.amazonaws.services.cognitoidentity.model.Credentials;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
public class BotControlMessagingService {

    private AmazonSQS amazonSQS;

    public AmazonSQS getSQS() {
        return amazonSQS;
    }

    private Map<String, String> getQueuePolicies(@Nullable String ip){
        Statement statement = new Statement(Statement.Effect.Allow)
                .withPrincipals(Principal.AllUsers)
                .withActions(
                        SQSActions.ReceiveMessage,
                        SQSActions.GetQueueUrl,
                        SQSActions.ChangeMessageVisibility,
                        SQSActions.ChangeMessageVisibilityBatch,
                        SQSActions.DeleteMessage,
                        SQSActions.DeleteMessageBatch);
        if (ip != null) statement.withConditions(new StringCondition(StringCondition.StringComparisonType.StringEquals, "aws:SourceIp", ip));

        Map<String, String> queueAttributes = new HashMap<>();
        queueAttributes.put(QueueAttributeName.Policy.toString(), new Policy().withStatements(statement).toJson());
        queueAttributes.put(QueueAttributeName.FifoQueue.toString(), "true");
        queueAttributes.getOrDefault(QueueAttributeName.ReceiveMessageWaitTimeSeconds, "20");
        queueAttributes.put(QueueAttributeName.ContentBasedDeduplication.toString(), "true");

        return queueAttributes;
    }

    public CreateQueueResult createQueue(String name){
        return createQueue(name, null);
    }

    public CreateQueueResult createQueue(String name, @Nullable String ip){
        return getSQS().createQueue(new CreateQueueRequest().withQueueName(name).withAttributes(getQueuePolicies(ip)));
    }

    public void connect(String region, Credentials credentials) {
        BasicSessionCredentials awsCreds = new BasicSessionCredentials(credentials.getAccessKeyId(), credentials.getSecretKey(), credentials.getSessionToken());
        amazonSQS = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(region)
                .build();
    }
}
