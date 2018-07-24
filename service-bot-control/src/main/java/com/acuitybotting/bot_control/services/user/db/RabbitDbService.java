package com.acuitybotting.bot_control.services.user.db;

import com.acuitybotting.bot_control.domain.RabbitDbRequest;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.data.flow.messaging.services.identity.RoutingUtil;
import com.acuitybotting.db.arango.acuity.bot_control.domain.RabbitDocument;
import com.acuitybotting.db.arango.acuity.bot_control.domain.RegisteredConnection;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.RegisteredConnectionRepository;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.RabbitDocumentRepository;
import com.arangodb.model.AqlQueryOptions;
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
public class RabbitDbService {

    public static final String CONNECTIONS_DATABASE = "registered-connections";
    private final ArangoOperations arangoOperations;
    private final RabbitDocumentRepository repository;
    private final RegisteredConnectionRepository registeredConnectionRepository;
    private final Gson gson = new Gson();

    public RabbitDbService(ArangoOperations operations, RabbitDocumentRepository repository, RegisteredConnectionRepository registeredConnectionRepository) {
        this.arangoOperations = operations;
        this.repository = repository;
        this.registeredConnectionRepository = registeredConnectionRepository;
    }

    private boolean isWriteAccessible(String userId, String db){
        if (db == null) return false;
        return "script-settings".equals(db) || db.startsWith("user.db.");
    }

    private boolean isReadAccessible(String userId, String db){
        if (db == null) return false;
        return "registered-connections".equals(db) || "script-settings".equals(db) || db.startsWith("user.db.");
    }

    public void save(String userId, RabbitDbRequest request) {
        if (CONNECTIONS_DATABASE.equals(request.getDatabase())){
            RegisteredConnection registeredConnection = registeredConnectionRepository.findByPrincipalKeyAndConnectionId(userId, request.getId()).orElse(null);
            if (registeredConnection == null) return;
            registeredConnection.getAttributes().put(request.getKey(), request.getDocument());
            registeredConnection.setLastHeartbeatTime(System.currentTimeMillis());
            registeredConnectionRepository.save(registeredConnection);
        }
        else {
            RabbitDocument rabbitDocument = new RabbitDocument();
            rabbitDocument.setPrincipalId(userId);
            rabbitDocument.setSubGroup(request.getGroup());
            rabbitDocument.setSubKey(request.getKey());
            rabbitDocument.setDatabase(request.getDatabase());
            rabbitDocument.setSubDocument(request.getDocument());

            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("principalId", rabbitDocument.getPrincipalId());
            queryMap.put("database", rabbitDocument.getDatabase());
            queryMap.put("subGroup", rabbitDocument.getSubGroup());
            queryMap.put("subKey", rabbitDocument.getSubKey());
            if (request.getRev() != null) queryMap.put("_rev", request.getRev());

            String upsertQuery = gson.toJson(queryMap);
            String document = gson.toJson(rabbitDocument);
            String query = "UPSERT " + upsertQuery + " INSERT " + document + " REPLACE " + document + " IN RabbitDocument";
            log.info("Query: " + query);
            arangoOperations.query(query, null, new AqlQueryOptions().count(true), null);
        }
    }

    private void delete(String userId, RabbitDbRequest request) {
        repository.deleteAllByPrincipalIdAndDatabaseAndSubGroupAndSubKey(userId, request.getDatabase(), request.getGroup(), request.getKey());
    }

    private RabbitDocument loadByKey(String userId, RabbitDbRequest request) {
        RabbitDocument document;
        if (CONNECTIONS_DATABASE.equals(request.getDatabase())){
            document = registeredConnectionRepository.findByPrincipalKeyAndConnectionId(userId, request.getKey()).map(this::connectionToUserDoc).orElse(null);
        }
        else{
            document = repository.findByPrincipalIdAndDatabaseAndSubGroupAndSubKey(userId, request.getDatabase(), request.getGroup(), request.getKey()).orElse(null);
        }
        return document;
    }

    private Set<RabbitDocument> loadByGroup(String userId, RabbitDbRequest request) {
        Set<RabbitDocument> result;
        if (CONNECTIONS_DATABASE.equals(request.getDatabase())){
            result = registeredConnectionRepository.findAllByPrincipalKeyAndLastHeartbeatTimeGreaterThan(userId, System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(30)).stream().map(this::connectionToUserDoc).collect(Collectors.toSet());
        }
        else{
            String documentQuery = request.getDocumentQuery();
            if (documentQuery == null){
                result = repository.findAllByPrincipalIdAndDatabaseAndSubGroup(userId, request.getDatabase(), request.getGroup());
            }
            else {
                result = repository.findAllByPrincipalIdAndDatabaseAndSubGroupAndSubDocumentMatchesRegex(userId, request.getDatabase(), request.getGroup(), documentQuery);
            }
        }
        return result;
    }

    private RabbitDocument connectionToUserDoc(RegisteredConnection registeredConnection) {
        RabbitDocument document = new RabbitDocument();
        document.setSubDocument(gson.toJson(registeredConnection));
        return document;
    }

    public void handle(MessageEvent messageEvent, RabbitDbRequest request, String userId) {
        log.info("Handling db request {} for user {}.", request, userId);

        if (isWriteAccessible(userId, request.getDatabase())){
            if (request.getType() == RabbitDbRequest.SAVE) {
                if (isWriteAccessible(userId, request.getDatabase())) save(userId, request);
            } else if (request.getType() == RabbitDbRequest.DELETE_BY_KEY) {
                delete(userId, request);
            }
        }

        if (isReadAccessible(userId, request.getDatabase())){
            if (request.getType() == RabbitDbRequest.FIND_BY_KEY) {
                RabbitDocument rabbitDocument = loadByKey(userId, request);
                messageEvent.getChannel().respond(messageEvent.getMessage(), rabbitDocument == null ? "" : gson.toJson(rabbitDocument));
            } else if (request.getType() == RabbitDbRequest.FIND_BY_GROUP) {
                messageEvent.getChannel().respond(messageEvent.getMessage(), gson.toJson(loadByGroup(userId, request)));
            }
        }
    }

    @EventListener
    public void handleScriptStorageRequest(MessageEvent messageEvent) {
        if (messageEvent.getRouting().contains(".services.acuity-db.request")) {
            String userId = RoutingUtil.routeToUserId(messageEvent.getRouting());
            handle(messageEvent, gson.fromJson(messageEvent.getMessage().getBody(), RabbitDbRequest.class), userId);
            messageEvent.getChannel().acknowledge(messageEvent.getMessage());
        }
    }
}
