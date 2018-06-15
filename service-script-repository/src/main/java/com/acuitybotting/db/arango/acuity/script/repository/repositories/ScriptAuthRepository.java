package com.acuitybotting.db.arango.acuity.script.repository.repositories;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.acuitybotting.db.arango.acuity.script.repository.domain.Script;
import com.acuitybotting.db.arango.acuity.script.repository.domain.ScriptAuth;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Collection;

/**
 * Created by Zachary Herridge on 6/15/2018.
 */
public interface ScriptAuthRepository extends ArangoRepository<ScriptAuth>{

    Collection<ScriptAuth> findAllByPrincipal(AcuityIdentity principal);

    Collection<ScriptAuth> findAllByScriptAndPrincipal(Script script, AcuityIdentity principal);
}
