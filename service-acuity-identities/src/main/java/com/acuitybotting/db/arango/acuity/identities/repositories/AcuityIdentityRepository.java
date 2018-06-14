package com.acuitybotting.db.arango.acuity.identities.repositories;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/14/2018.
 */
public interface AcuityIdentityRepository extends ArangoRepository<AcuityIdentity> {

    Optional<AcuityIdentity> findByEmail(String email);

    Optional<AcuityIdentity> findByUsername(String username);

    Optional<AcuityIdentity> findByPrincipalKeysContaining(String key);
}
