package com.acuitybotting.db.arango.acuity.bot_control.repositories;

import com.acuitybotting.db.arango.acuity.bot_control.domain.RabbitDocument;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
public interface RabbitDocumentRepository extends ArangoRepository<RabbitDocument> {

    Optional<RabbitDocument> findByPrincipalIdAndDatabaseAndSubGroupAndSubKey(String principalId, String database, String subGroup, String subKey);

    Set<RabbitDocument> findAllByPrincipalIdAndDatabaseAndSubGroup(String principalId, String database, String subGroup);

    Set<RabbitDocument> findAllByPrincipalIdAndDatabaseAndSubGroupAndSubDocumentMatchesRegex(String principalId, String database, String subGroup, String regex);

    void deleteAllByPrincipalIdAndDatabaseAndSubGroupAndSubKey(String principalId, String database, String subGroup, String subKey);
}
