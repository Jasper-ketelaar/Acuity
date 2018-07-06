package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.services.managment.BotControlManagementService;
import com.acuitybotting.data.flow.messaging.services.aws.iot.IotAuthenticationService;
import com.acuitybotting.data.flow.messaging.services.aws.iot.IotClientService;
import com.acuitybotting.data.flow.messaging.services.aws.iot.domain.RegisterResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Component
@Slf4j
@PropertySource("classpath:iot.credentials")
public class BotControlRunner implements CommandLineRunner{

    @Value("${rspeer.token}")
    private String token;

    private final BotControlManagementService managementService;

    @Autowired
    public BotControlRunner(BotControlManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public void run(String... strings) throws Exception {
        try {
            RegisterResponse registerResponse = IotAuthenticationService.authenticate(token).orElse(null);

            String userId = registerResponse.getAssumedRoleUser().getAssumedRoleId().split(":")[1];

            log.info("UserId: " + userId);

            String clientEndpoint = "a2i158467e5k2v.iot.us-east-1.amazonaws.com";
            String clientId = UUID.randomUUID().toString();

            IotClientService iotClientService = new IotClientService();

            iotClientService.start(clientEndpoint, clientId,
                    registerResponse.getCredentials().getAccessKeyId(),
                    registerResponse.getCredentials().getSecretAccessKey(),
                    registerResponse.getCredentials().getSessionToken()
            );

            iotClientService.consume("user/" + userId + "/bot/" + clientId)
                    .withCallback(System.out::println)
                    .start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
