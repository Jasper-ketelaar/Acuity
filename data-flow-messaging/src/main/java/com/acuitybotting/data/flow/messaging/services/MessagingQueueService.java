package com.acuitybotting.data.flow.messaging.services;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.SQSActions;
import com.amazonaws.auth.policy.conditions.StringCondition;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.QueueAttributeName;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/7/2018.
 */
@Service
public class MessagingQueueService {

    private AmazonSQS amazonSQS;

    public AmazonSQS getSQS() {
        return Optional.ofNullable(amazonSQS).orElseThrow(() -> new RuntimeException("Connect to SQS before referencing the SQS object. Call messagingQueueService.setAmazonSQS()."));
    }

    public MessagingQueueService setAmazonSQS(AmazonSQS amazonSQS) {
        this.amazonSQS = amazonSQS;
        return this;
    }

    private Map<String, String> getQueuePolicies(String ip){
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

    public CreateQueueResult createQueue(String name, String ip){
        return getSQS().createQueue(new CreateQueueRequest().withQueueName(name).withAttributes(getQueuePolicies(ip)));
    }
}
