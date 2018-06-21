package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;


import com.acuitybotting.path_finding.algorithms.graph.Edge;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class HPAEdge implements Edge {

    private HPANode start, end;
    private double cost;
    private List<Edge> path;

    public HPAEdge(HPANode start, HPANode end) {
        this.start = start;
        this.end = end;
    }

    public boolean isInternal(){
        return Objects.equals(start.getHPARegion(), end.getHPARegion());
    }

    @Override
    public double getCostPenalty() {
        return cost;
    }

    public HPAEdge setCost(double cost) {
        this.cost = cost;
        return this;
    }

    public HPAEdge setPath(List<Edge> path) {
        this.path = path;
        return this;
    }
}
