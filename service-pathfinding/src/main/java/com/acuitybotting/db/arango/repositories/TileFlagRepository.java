package com.acuitybotting.db.arango.repositories;

import com.acuitybotting.db.arango.domain.TileFlag;
import com.arangodb.springframework.repository.ArangoRepository;
import org.springframework.data.geo.Polygon;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */

public interface TileFlagRepository extends ArangoRepository<TileFlag> {

    Iterable<TileFlag> findByLocationWithinAndPlane(Polygon polygon, int plane);

    long countByLocationWithinAndPlane(Polygon polygon, int plane);
}
