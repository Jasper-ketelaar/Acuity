package com.acuitybotting.db.arango.acuity.bot_control.repositories;

import com.acuitybotting.db.arango.acuity.bot_control.domain.RegisteredConnection;
import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
public interface RegisteredConnectionRepository extends ArangoRepository<RegisteredConnection> {

}
