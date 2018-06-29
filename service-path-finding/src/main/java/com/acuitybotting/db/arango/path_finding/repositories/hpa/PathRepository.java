package com.acuitybotting.db.arango.path_finding.repositories.hpa;

import com.acuitybotting.db.arango.path_finding.domain.hpa.SavedPath;
import com.arangodb.springframework.repository.ArangoRepository;

/**
 * Created by Zachary Herridge on 6/29/2018.
 */
public interface PathRepository extends ArangoRepository<SavedPath> {

    Iterable<SavedPath> findAllByWebVersion(int version);

    void deleteAllByWebVersion(int version);
}
