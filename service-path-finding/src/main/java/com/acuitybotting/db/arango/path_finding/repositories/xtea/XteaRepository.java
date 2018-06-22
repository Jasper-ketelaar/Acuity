package com.acuitybotting.db.arango.path_finding.repositories.xtea;

import com.acuitybotting.db.arango.path_finding.domain.xtea.Xtea;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Collection;

/**
 * Created by Zachary Herridge on 6/22/2018.
 */
public interface XteaRepository extends ArangoRepository<Xtea> {

    Collection<Xtea> findAllByRevision(int revision);

    Collection<Xtea> findByRegionAndRevision(int region, int revision);
}
