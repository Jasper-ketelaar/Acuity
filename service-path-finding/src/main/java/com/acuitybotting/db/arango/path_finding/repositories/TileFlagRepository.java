package com.acuitybotting.db.arango.path_finding.repositories;

import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.arangodb.springframework.repository.ArangoRepository;
import org.springframework.data.geo.Polygon;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
public interface TileFlagRepository extends ArangoRepository<TileFlag> {

    Iterable<TileFlag> findAllByPlane(int plane);

    long countAllByXBetweenAndYBetweenAndPlane(int xLower, int xUpper, int yLower, int yUpper, int plane);

    Iterable<TileFlag> findAllByXBetweenAndYBetweenAndPlane(int xLower, int xUpper, int yLower, int yUpper, int plane);
}
