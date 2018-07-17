package com.acuitybotting.path_finding.algorithms.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public interface Node {

    default Collection<Edge> getNeighbors() {
        return getNeighbors(Collections.emptyMap());
    }

    Collection<Edge> getNeighbors(Map<String, Object> args);

}
