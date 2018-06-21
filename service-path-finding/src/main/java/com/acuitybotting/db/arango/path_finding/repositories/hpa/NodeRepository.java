package com.acuitybotting.db.arango.path_finding.repositories.hpa;

import com.acuitybotting.db.arango.path_finding.domain.hpa.SavedNode;
import com.arangodb.springframework.repository.ArangoRepository;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public interface NodeRepository extends ArangoRepository<SavedNode>{

    Iterable<SavedNode> findAllByWebVersion(int version);

}
