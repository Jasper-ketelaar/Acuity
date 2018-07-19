package com.acuitybotting.bot_control.services.managment;

import com.acuitybotting.bot_control.domain.ScriptStorageRequest;
import com.acuitybotting.data.flow.messaging.services.events.MessageEvent;
import com.acuitybotting.db.arango.acuity.bot_control.domain.UserDefinedDocument;
import com.acuitybotting.db.arango.acuity.bot_control.repositories.UserDefinedDocumentRepository;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.ArangoOperations;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Service
public class UserDbService {

    private final ArangoOperations arangoOperations;
    private final UserDefinedDocumentRepository repository;
    private final Gson gson = new Gson();

    public UserDbService(ArangoOperations operations, UserDefinedDocumentRepository repository) {
        this.arangoOperations = operations;
        this.repository = repository;
    }

    public void handle(MessageEvent messageEvent, ScriptStorageRequest request, String userId){
        if (request.getType() == ScriptStorageRequest.SAVE){
            UserDefinedDocument userDefinedDocument = new UserDefinedDocument();
            userDefinedDocument.setUserId(userId);
            userDefinedDocument.setSubGroup(request.getGroup());
            userDefinedDocument.setSubKey(request.getKey());
            userDefinedDocument.setSubDocument(request.getDocument());

            String key = userDefinedDocument.getUserId() + "_" + userDefinedDocument.getSubGroup() + "_" + userDefinedDocument.getSubKey();
            userDefinedDocument.set_key(key);
            String document = gson.toJson(userDefinedDocument);
            arangoOperations.query("UPSERT {_key : '" + key + "'} INSERT " + document + " REPLACE " + document + " IN UserDefinedDocument", null, null, null);
        }
        else if (request.getType() == ScriptStorageRequest.DELETE_BY_KEY){
            repository.deleteAllByUserIdAndSubGroupAndSubKey(userId, request.getGroup(), request.getKey());
        }
        else if (request.getType() == ScriptStorageRequest.LOAD_BY_KEY){
            UserDefinedDocument document = repository.findByUserIdAndSubGroupAndSubKey(userId, request.getGroup(), request.getKey()).orElse(null);
            messageEvent.getChannel().respond(messageEvent.getMessage(), document == null ? "" : gson.toJson(document));
        }
        else if (request.getType() == ScriptStorageRequest.LOAD_BY_GROUP){
            Set<UserDefinedDocument> documents = repository.findAllByUserIdAndSubGroup(userId, request.getGroup());
            messageEvent.getChannel().respond(messageEvent.getMessage(), gson.toJson(documents));
        }
    }
}
