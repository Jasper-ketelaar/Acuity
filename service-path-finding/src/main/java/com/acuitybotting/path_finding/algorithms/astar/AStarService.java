package com.acuitybotting.path_finding.algorithms.astar;

import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarHeuristicSupplier;
import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

@Getter
@Setter
@Service
public class AStarService {

    private boolean debugMode;
    private int maxAttempts = 50000;

    public Optional<List<? extends Edge>> findPath(AStarHeuristicSupplier heuristicSupplier, Node start, Node end) {
        return findPath(heuristicSupplier, start, end, Collections.emptyMap());
    }

    public Optional<List<? extends Edge>> findPath(AStarHeuristicSupplier heuristicSupplier, Node start, Node end, Map<String, Object> args) {
        return findPath(heuristicSupplier, start, end, null, args);
    }

    public Optional<List<? extends Edge>> findPath(AStarHeuristicSupplier heuristicSupplier, Node start, Node end, Predicate<Edge> edgePredicate, Map<String, Object> args) {
        return build().setEdgePredicate(edgePredicate).setArgs(args).findPath(heuristicSupplier, start, end);
    }

    public AStarImplementation build(){
        return new AStarImplementation().setMaxAttempts(maxAttempts).setDebugMode(debugMode);
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
