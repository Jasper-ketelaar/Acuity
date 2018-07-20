package com.acuitybotting.db.arango.acuity.bot_control.repositories;

import com.acuitybotting.db.arango.acuity.bot_control.domain.UserDocument;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
public interface UserDefinedDocumentRepository extends ArangoRepository<UserDocument> {

    Optional<UserDocument> findByUserIdAndSubGroupAndSubKey(String userId, String subGroup, String subKey);

    Set<UserDocument> findAllByUserIdAndSubGroup(String userId, String subGroup);

    void deleteAllByUserIdAndSubGroupAndSubKey(String userId, String subGroup, String subKey);
}
