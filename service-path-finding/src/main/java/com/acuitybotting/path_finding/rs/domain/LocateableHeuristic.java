package com.acuitybotting.path_finding.rs.domain;

import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarHeuristicSupplier;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;

public class LocateableHeuristic implements AStarHeuristicSupplier {

    @Override
    public Double getHeuristic(Node start, Node current, Node end, Edge edge) {
        Location currentLocation = ((Locateable) current).getLocation();
        Location endLocation = ((Locateable) end).getLocation();
        return  currentLocation.getTraversalCost(endLocation);
    }
}