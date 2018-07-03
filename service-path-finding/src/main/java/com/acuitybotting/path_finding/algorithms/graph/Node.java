package com.acuitybotting.path_finding.algorithms.graph;

import java.util.Collection;

public interface Node {

    default Collection<Edge> getNeighbors() {
        return getNeighbors(false);
    }

    Collection<Edge> getNeighbors(boolean ignoreSelfBlocked);

}
