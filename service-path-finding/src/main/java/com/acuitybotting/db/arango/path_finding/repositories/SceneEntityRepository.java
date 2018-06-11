package com.acuitybotting.db.arango.path_finding.repositories;

import com.acuitybotting.db.arango.path_finding.domain.SceneEntity;
import com.arangodb.springframework.repository.ArangoRepository;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
public interface SceneEntityRepository extends ArangoRepository<SceneEntity> {

    Iterable<SceneEntity> findAllByXBetweenAndYBetweenAndPlane(int xLower, int xUpper, int yLower, int yUpper, int plane);

    Iterable<SceneEntity> findAllByName(String name);

    Iterable<SceneEntity> findAllByXBetweenAndYBetweenAndPlaneAndNameIn(int xLower, int xUpper, int yLower, int yUpper, int plane, String[] doorNames);
}
