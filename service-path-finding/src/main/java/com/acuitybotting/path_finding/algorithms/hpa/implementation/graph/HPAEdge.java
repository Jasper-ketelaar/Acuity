package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;


import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;

import java.util.Objects;

public class HPAEdge implements Edge {

    private HPANode start, end;

    public HPAEdge(HPANode start, HPANode end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Node getStart() {
        return start;
    }

    @Override
    public Node getEnd() {
        return end;
    }

    public boolean isInternal(){
        return Objects.equals(start.getRegion(), end.getRegion());
    }
}
