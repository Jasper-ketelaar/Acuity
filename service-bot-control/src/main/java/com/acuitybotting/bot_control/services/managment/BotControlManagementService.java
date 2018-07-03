package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.db.arango.acuity.bot_control.domain.BotInstance;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.BotInstanceRepository;
import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityIdentityService;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.policy.*;
import com.amazonaws.auth.policy.conditions.StringCondition;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.QueueAttributeName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
@PropertySource("classpath:sqs.credentials")
public class BotControlManagementService {

    private final AcuityIdentityService acuityIdentityService;
    private final BotInstanceRepository botInstanceRepository;

    @Value("${sqs.region}")
    private String region;

    @Value("${sqs.access}")
    private String accessKey;

    @Value("${sqs.secret}")
    private String secretKey;

    private AmazonSQS sqs;

    @Autowired
    public BotControlManagementService(AcuityIdentityService acuityIdentityService, BotInstanceRepository botInstanceRepository) {
        this.acuityIdentityService = acuityIdentityService;
        this.botInstanceRepository = botInstanceRepository;
    }

    @PostConstruct
    private void init(){
        AmazonSQSClientBuilder builder = AmazonSQSClientBuilder.standard();
        builder.setRegion(region);
        builder.setCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)));
        sqs = builder.build();
    }

    public BotInstance register(AcuityPrincipal acuityPrincipal, String remoteIp) {
        Objects.requireNonNull(acuityPrincipal);

        BotInstance botInstance = new BotInstance();

        Map<String, Object> attributes = new HashMap<>();
        if (remoteIp != null) attributes.put("connectIp", remoteIp);

        botInstance.setAttributes(attributes);
        botInstance.setPrincipalKey(acuityPrincipal.getKey());
        botInstance.setConnectionTime(System.currentTimeMillis());

        setUpSqsQueue(acuityPrincipal, botInstance);

        return botInstanceRepository.save(botInstance);
    }

    private String key(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private void setUpSqsQueue(AcuityPrincipal acuityPrincipal, BotInstance botInstance) {
        String queueName = "bot" + "-" + key() + ".fifo";

        AcuityIdentity acuityIdentity = acuityIdentityService.createIfAbsent(acuityPrincipal);

        String botAuthKey = acuityIdentity.getBotAuthKey();
        if (botAuthKey == null){
            acuityIdentity.setBotAuthKey(key());
            botAuthKey = acuityIdentityService.getIdentityRepository().save(acuityIdentity).getBotAuthKey();
        }

        String queueUrl = createQueue(queueName, botAuthKey);
        if (queueUrl == null) throw new RuntimeException("Failed to setup queue.");

        botInstance.setQueueUrl(queueUrl);
        botInstance.setQueueAuth(botAuthKey);
    }

    private Map<String, String> getQueuePolicies(String queueArn, boolean fifo, String authHeader) {
        Map<String, String> queueAttributes = new HashMap<>();

        if (authHeader != null && queueArn != null){
            Statement statement = new Statement(Statement.Effect.Allow)
                    .withPrincipals(Principal.All)
                    .withResources(new Resource(queueArn))
                    .withActions((Action) () -> "SQS:*");
            statement.withConditions(new StringCondition(StringCondition.StringComparisonType.StringEquals, "aws:UserAgent", authHeader));
            queueAttributes.put(QueueAttributeName.Policy.toString(), new Policy().withStatements(statement).toJson());
        }

        queueAttributes.put(QueueAttributeName.FifoQueue.toString(), String.valueOf(fifo));
        queueAttributes.put(QueueAttributeName.ReceiveMessageWaitTimeSeconds.toString(), "20");
        queueAttributes.put(QueueAttributeName.ContentBasedDeduplication.toString(), "true");

        return queueAttributes;
    }

    private String createQueue(String name, String authHeader) {
        boolean fifo = name.endsWith(".fifo");

        Map<String, String> attributes = getQueuePolicies(null, fifo, null);

        String queueUrl = sqs.createQueue(new CreateQueueRequest().withQueueName(name).withAttributes(attributes)).getQueueUrl();
        String queueArn = sqs.getQueueAttributes(queueUrl, Collections.singletonList("QueueArn")).getAttributes().get("QueueArn");

        attributes = getQueuePolicies(queueArn, fifo, authHeader);
        if (sqs.setQueueAttributes(queueUrl, attributes) != null) return queueUrl;

        return null;
    }
}
