package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;


import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import lombok.Getter;

import java.util.Objects;

@Getter
public class HPAEdge implements Edge {

    private HPANode start, end;
    private double cost;

    public HPAEdge(HPANode start, HPANode end) {
        this.start = start;
        this.end = end;
    }

    public boolean isInternal(){
        return Objects.equals(start.getHPARegion(), end.getHPARegion());
    }

    public HPAEdge setCost(double cost) {
        this.cost = cost;
        return this;
    }
}
