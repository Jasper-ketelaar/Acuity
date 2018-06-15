package com.acuitybotting.db.arango.acuity.script.repository.repositories;

import com.acuitybotting.db.arango.acuity.script.repository.domain.ScriptAuth;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Collection;

/**
 * Created by Zachary Herridge on 6/15/2018.
 */
public interface ScriptAuthRepository extends ArangoRepository<ScriptAuth>{

    Collection<ScriptAuth> findAllByPrincipal(String principalId);

    Collection<ScriptAuth> findAllByScriptAndPrincipal(String scriptId, String principalId);

    Collection<ScriptAuth> findAllByScript(String scriptId);
}
