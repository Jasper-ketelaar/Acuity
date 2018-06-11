package com.acuitybotting.path_finding.algorithms.astar.implmentation;


import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;

public interface AStarHeuristicSupplier {

    Double getHeuristic(Node start, Node current, Node end, Edge edge);

}
