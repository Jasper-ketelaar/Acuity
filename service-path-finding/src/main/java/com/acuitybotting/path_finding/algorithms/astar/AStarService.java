package com.acuitybotting.path_finding.algorithms.astar;

import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarHeuristicSupplier;
import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
@Setter
@Service
public class AStarService {

    private boolean debugMode;
    private int maxAttempts = 50000;

    public Optional<List<Edge>> findPath(AStarHeuristicSupplier heuristicSupplier, Node start, Node end) {
        return findPath(heuristicSupplier, start, end, null);
    }

    public Optional<List<Edge>> findPath(AStarHeuristicSupplier heuristicSupplier, Node start, Node end, Predicate<Edge> edgePredicate) {
        return build().setEdgePredicate(edgePredicate).findPath(heuristicSupplier, start, end);
    }

    public AStarImplementation build(){
        return new AStarImplementation().setMaxAttempts(maxAttempts).setDebugMode(debugMode);
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
