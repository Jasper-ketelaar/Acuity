package com.acuitybotting.db.arango.path_finding.repositories;

import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.acuitybotting.path_finding.rs.domain.Location;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
public interface TileFlagRepository extends ArangoRepository<TileFlag> {

    default Optional<TileFlag> findByLocation(Location location){
        return findByXAndYAndPlane(location.getX(), location.getY(), location.getPlane());
    }

    Optional<TileFlag> findByXAndYAndPlane(int x, int y, int plane);

    Iterable<TileFlag> findAllByPlane(int plane);

    long countAllByXBetweenAndYBetweenAndPlaneAndDeprecatedNot(int xLower, int xUpper, int yLower, int yUpper, int plane);

    Iterable<TileFlag> findAllByXBetweenAndYBetweenAndPlane(int xLower, int xUpper, int yLower, int yUpper, int plane);
}
