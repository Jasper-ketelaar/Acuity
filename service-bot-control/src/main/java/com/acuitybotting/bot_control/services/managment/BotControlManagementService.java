package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.identity.RoutingUtil;
import com.acuitybotting.db.arango.acuity.bot_control.domain.RegisteredConnection;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.RegisteredConnectionRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
@Slf4j
public class BotControlManagementService {

    private final RegisteredConnectionRepository registeredConnectionRepository;
    private final Gson gson = new Gson();

    @Autowired
    public BotControlManagementService(RegisteredConnectionRepository registeredConnectionRepository) {
        this.registeredConnectionRepository = registeredConnectionRepository;
    }

    public void heartbeat(String userId, JsonObject heartbeat){
        String connectionId = heartbeat.get("connectionId").getAsString();
        Objects.requireNonNull(connectionId);
        registeredConnectionRepository.updateHeartbeat(userId, connectionId, System.currentTimeMillis());
        log.info("Updated heartbeat for connection {} for user {}.", connectionId, userId);
    }

    public RegisteredConnection register(String userId, JsonObject registration) {
        Objects.requireNonNull(userId);

        String connectionId = registration.get("connectionId").getAsString();
        String type = registration.get("type").getAsString();
        Objects.requireNonNull(connectionId);
        Objects.requireNonNull(type);

        RegisteredConnection registeredConnection = new RegisteredConnection();
        registeredConnection.setPrincipalKey(userId);
        registeredConnection.setConnectionId(connectionId);
        registeredConnection.setConnectionType(type);
        registeredConnection.setAttributes(new HashMap<>());


        long now = System.currentTimeMillis();
        registeredConnection.setConnectionTime(now);
        registeredConnection.setLastHeartbeatTime(now);

        registeredConnectionRepository.save(registeredConnection);

        log.info("Registered connection {} for user {}.", registeredConnection, userId);

        return registeredConnection;
    }

    @EventListener
    public void handleConnectionRegistration(MessageEvent messageEvent){
        String userId = RoutingUtil.routeToUserId(messageEvent.getRouting());
        JsonObject body = gson.fromJson(messageEvent.getMessage().getBody(), JsonObject.class);

        if (messageEvent.getRouting().endsWith(".connections.register")){
            register(userId, body);
            messageEvent.getChannel().acknowledge(messageEvent.getMessage());
        }
    }
}
