package com.acuitybotting.db.arango.bot_control.repositories;

import com.acuitybotting.db.arango.bot_control.domain.BotInstance;
import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
public interface BotInstanceRepository extends ArangoRepository<BotInstance> {

    @Query("UPDATE DOCUMENT(@0) WITH {lastHeartbeatTime : @1} in BotInstance")
    void updateHeartbeat(String botID, long heartbeat);

    Optional<BotInstance> findByPrincipalKeyAndKey(String principalKey, String key);

}
