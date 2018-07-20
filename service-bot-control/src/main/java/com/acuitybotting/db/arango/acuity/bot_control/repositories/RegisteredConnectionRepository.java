package com.acuitybotting.db.arango.acuity.bot_control.repositories;

import com.acuitybotting.db.arango.acuity.bot_control.domain.RegisteredConnection;
import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
public interface RegisteredConnectionRepository extends ArangoRepository<RegisteredConnection> {

    @Query("FOR c IN  RegisteredConnection\n" +
            "FILTER c.principalKey == @0 AND c.connectionId == @1\n" +
            "UPDATE c._key WITH {lastHeartbeatTime : @2} IN RegisteredConnection")
    void updateHeartbeat(String principalKey, String connectionId, long heartbeat);

    Set<RegisteredConnection> findAllByPrincipalKeyAndLastHeartbeatTimeGreaterThan(String principalId, long heartbeatValue);

    Optional<RegisteredConnection> findByPrincipalKeyAndConnectionId(String principalId, String connectionId);
}
