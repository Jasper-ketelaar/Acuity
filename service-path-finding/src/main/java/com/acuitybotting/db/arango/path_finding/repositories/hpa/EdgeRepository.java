package com.acuitybotting.db.arango.path_finding.repositories.hpa;


import com.acuitybotting.db.arango.path_finding.domain.hpa.SavedEdge;
import com.arangodb.ArangoCursor;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Collection;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public interface EdgeRepository extends ArangoRepository<SavedEdge> {

    Collection<SavedEdge> findAllByWebVersion(int version);

    void deleteAllByWebVersion(int version);
}
