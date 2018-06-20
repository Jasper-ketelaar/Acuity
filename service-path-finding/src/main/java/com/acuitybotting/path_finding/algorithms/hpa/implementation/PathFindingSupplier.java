package com.acuitybotting.path_finding.algorithms.hpa.implementation;


import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.function.Predicate;

public interface PathFindingSupplier {

    int findPath(Location start, Location end, Predicate<Edge> predicate);

    boolean isDirectlyConnected(Location start, Location end);

}
