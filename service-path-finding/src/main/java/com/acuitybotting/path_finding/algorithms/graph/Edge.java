package com.acuitybotting.path_finding.algorithms.graph;

public interface Edge {

    Node getStart();

    Node getEnd();

    default double getCostPenalty(){
        return 0;
    }
}
