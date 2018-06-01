package com.acuitybotting.db.arango.bot_control.repositories;

import com.acuitybotting.db.arango.bot_control.domain.BotInstance;
import com.arangodb.springframework.repository.ArangoRepository;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
public interface BotInstanceRepository extends ArangoRepository<BotInstance> {
}
