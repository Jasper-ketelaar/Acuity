package com.acuitybotting.db.arango.path_finding.repositories.hpa;

import com.acuitybotting.db.arango.path_finding.domain.hpa.SavedRegion;
import com.arangodb.springframework.repository.ArangoRepository;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public interface RegionRepository extends ArangoRepository<SavedRegion> {

    Iterable<SavedRegion> findAllByWebVersion(int version);

    void deleteAllByWebVersion(int version);

}
