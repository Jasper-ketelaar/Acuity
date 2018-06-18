package com.acuitybotting.path_finding.algorithms.hpa.implementation;


import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.function.Predicate;

public interface PathFindingSupplier {

    default boolean isReachableFrom(Location start, Location end){
        return isReachableFrom(start, end, null);
    }

    default boolean isReachableFrom(Location start, Location end, Predicate<Edge> predicate){
        return findPath(start, end, predicate) != 0;
    }

    int findPath(Location start, Location end, Predicate<Edge> predicate);

    boolean isDirectlyConnected(Location start, Location end);

}
