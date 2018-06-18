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
    private static List<AStarImplementation> currentSearches = new ArrayList<>();

    public Optional<List<Edge>> findPath(AStarHeuristicSupplier heuristicSupplier, Node start, Node end) {
        return findPath(heuristicSupplier, start, end, null);
    }

    public Optional<List<Edge>> findPath(AStarHeuristicSupplier heuristicSupplier, Node start, Node end, Predicate<Edge> edgePredicate) {
        AStarImplementation aStarImplementation = new AStarImplementation();
        if (isDebugMode()) currentSearches.add(aStarImplementation);
        Optional<List<Edge>> path = aStarImplementation.findPath(heuristicSupplier, start, end, edgePredicate);
        if (isDebugMode()) currentSearches.remove(aStarImplementation);
        return path;
    }

    public static List<AStarImplementation> getCurrentSearches() {
        return currentSearches;
    }
}
