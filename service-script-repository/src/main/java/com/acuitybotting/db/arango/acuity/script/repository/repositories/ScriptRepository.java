package com.acuitybotting.db.arango.acuity.script.repository.repositories;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.acuitybotting.db.arango.acuity.script.repository.domain.Script;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Collection;

public interface ScriptRepository extends ArangoRepository<Script> {

    Collection<Script> findAllByAuthorOrAccessLevel(AcuityIdentity author, int accessLevel);

}
