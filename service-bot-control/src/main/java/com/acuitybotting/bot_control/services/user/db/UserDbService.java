package com.acuitybotting.bot_control.services.user.db;

import com.acuitybotting.bot_control.domain.ScriptStorageRequest;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.identity.RoutingUtil;
import com.acuitybotting.db.arango.acuity.bot_control.domain.RegisteredConnection;
import com.acuitybotting.db.arango.acuity.bot_control.domain.UserDocument;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.RegisteredConnectionRepository;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.UserDefinedDocumentRepository;
import com.arangodb.springframework.core.ArangoOperations;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Service
@Slf4j
public class UserDbService {

    public static final String CONNECTIONS_GROUP = "acuity.registered.connections";
    private final ArangoOperations arangoOperations;
    private final UserDefinedDocumentRepository repository;
    private final RegisteredConnectionRepository registeredConnectionRepository;
    private final Gson gson = new Gson();

    public UserDbService(ArangoOperations operations, UserDefinedDocumentRepository repository, RegisteredConnectionRepository registeredConnectionRepository) {
        this.arangoOperations = operations;
        this.repository = repository;
        this.registeredConnectionRepository = registeredConnectionRepository;
    }

    public void save(String userId, ScriptStorageRequest request) {
        String group = request.getGroup();
        if (group.equals(CONNECTIONS_GROUP)){
            RegisteredConnection registeredConnection = registeredConnectionRepository.findByPrincipalKeyAndConnectionId(userId, request.getId()).orElse(null);
            if (registeredConnection == null) return;
            registeredConnection.getAttributes().put(request.getKey(), request.getDocument());
            registeredConnection.setLastHeartbeatTime(System.currentTimeMillis());
            registeredConnectionRepository.save(registeredConnection);
        }
        else {
            UserDocument userDocument = new UserDocument();
            userDocument.setUserId(userId);
            userDocument.setSubGroup(group);
            userDocument.setSubKey(request.getKey());
            userDocument.setSubDocument(request.getDocument());
            userDocument.setSubHash(userDocument.getUserId() + "_" + userDocument.getSubGroup() + "_" + userDocument.getSubKey());

            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("subHash", userDocument.getSubHash());

            String upsertQuery = gson.toJson(queryMap);
            String document = gson.toJson(userDocument);
            String query = "UPSERT " + upsertQuery + " INSERT " + document + " REPLACE " + document + " IN UserDocument";
            arangoOperations.query(query, null, null, null);
        }
    }

    private void delete(String userId, ScriptStorageRequest request) {
        repository.deleteAllByUserIdAndSubGroupAndSubKey(userId, request.getGroup(), request.getKey());
    }

    private UserDocument loadByKey(String userId, ScriptStorageRequest request) {
        String group = request.getGroup();
        UserDocument document;
        if (group.equals(CONNECTIONS_GROUP))
            document = registeredConnectionRepository.findByPrincipalKeyAndConnectionId(userId, request.getKey()).map(this::connectionToUserDoc).orElse(null);
        else
            document = repository.findByUserIdAndSubGroupAndSubKey(userId, request.getGroup(), request.getKey()).orElse(null);
        return document;
    }

    private Set<UserDocument> loadByGroup(String userId, ScriptStorageRequest request) {
        String group = request.getGroup();
        Set<UserDocument> result;
        if (group.equals(CONNECTIONS_GROUP))
            result = registeredConnectionRepository.findAllByPrincipalKeyAndLastHeartbeatTimeGreaterThan(userId, System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(30)).stream().map(this::connectionToUserDoc).collect(Collectors.toSet());
        else result = repository.findAllByUserIdAndSubGroup(userId, request.getGroup());
        return result;
    }

    private UserDocument connectionToUserDoc(RegisteredConnection registeredConnection) {
        UserDocument document = new UserDocument();
        document.setSubDocument(gson.toJson(registeredConnection));
        return document;
    }

    public void handle(MessageEvent messageEvent, ScriptStorageRequest request, String userId) {
        log.info("Handling db request {} for user {}.", request, userId);
        if (request.getType() == ScriptStorageRequest.SAVE) {
            save(userId, request);
        } else if (request.getType() == ScriptStorageRequest.DELETE_BY_KEY) {
            delete(userId, request);
        } else if (request.getType() == ScriptStorageRequest.LOAD_BY_KEY) {
            UserDocument userDocument = loadByKey(userId, request);
            messageEvent.getChannel().respond(messageEvent.getMessage(), userDocument == null ? "" : gson.toJson(userDocument));
        } else if (request.getType() == ScriptStorageRequest.LOAD_BY_GROUP) {
            messageEvent.getChannel().respond(messageEvent.getMessage(), gson.toJson(loadByGroup(userId, request)));
        }
    }

    @EventListener
    public void handleScriptStorageRequest(MessageEvent messageEvent) {
        if (messageEvent.getRouting().endsWith(".services.acuity-db.request")) {
            String userId = RoutingUtil.routeToUserId(messageEvent.getRouting());
            handle(messageEvent, gson.fromJson(messageEvent.getMessage().getBody(), ScriptStorageRequest.class), userId);
            messageEvent.getChannel().acknowledge(messageEvent.getMessage());
        }
    }
}
