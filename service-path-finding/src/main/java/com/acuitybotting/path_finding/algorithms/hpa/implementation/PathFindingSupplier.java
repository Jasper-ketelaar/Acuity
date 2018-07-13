package com.acuitybotting.path_finding.algorithms.hpa.implementation;


import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface PathFindingSupplier {

    default Optional<List<? extends Edge>> findPath(Location start, Location end, Predicate<Edge> predicate){
        return findPath(start, end, predicate, false);
    }

    Optional<List<? extends Edge>> findPath(Location start, Location end, Predicate<Edge> predicate, boolean ignoreStartBlocked);

    boolean isDirectlyConnected(Location start, Location end);
}
