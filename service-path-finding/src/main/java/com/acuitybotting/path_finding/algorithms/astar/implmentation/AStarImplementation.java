package com.acuitybotting.path_finding.algorithms.astar.implmentation;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Getter
public class AStarImplementation {

    private boolean debugMode = false;
    private int maxAttempts = 1000000;

    private Node start, end;

    private AStarHeuristicSupplier heuristicSupplier;
    private Function<Node, Boolean> successEvaluator = node -> node.equals(end);
    private Predicate<Edge> edgePredicate = null;

    private Map<Node, Edge> pathCache = new HashMap<>();
    private Map<Node, Double> costCache = new HashMap<>();
    private PriorityQueue<AStarStore> open = new PriorityQueue<>();

    private boolean ignoreStartBlocked = false;

    public Optional<List<Edge>> findPath(AStarHeuristicSupplier heuristicSupplier, Node start, Node end) {
        Objects.requireNonNull(heuristicSupplier);
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);

        this.heuristicSupplier = heuristicSupplier;
        this.start = start;
        this.end = end;
        return execute();
    }

    private Optional<List<Edge>> execute() {
        clear();

        open.add(new AStarStore(start, 0));
        costCache.put(start, 0d);

        int attempts = 0;
        while (!open.isEmpty()) {
            attempts++;
            AStarStore current = open.poll();

            if (attempts >= maxAttempts) {
                log.warn("Failed to find path form {} to {} after {} attempts.", start, end, attempts);
                break;
            }

            if (successEvaluator.apply(current.getNode())) {
                log.debug("Found path from {} to {} in {} attempts.", start, end, attempts);
                List<Edge> path = collectPath(end, start);
                if (!debugMode) clear();
                return Optional.ofNullable(path);
            }

            for (Edge edge : current.getNode().getNeighbors(ignoreStartBlocked && current.getNode().equals(start))) {
                if (edgePredicate != null && !edgePredicate.test(edge)) continue;
                if (!edge.evaluate()) continue;

                Node next = edge.getEnd();

                double newCost = costCache.getOrDefault(current.getNode(), 0d) + heuristicSupplier.getHeuristic(start, current.getNode(), next, edge);

                Double oldCost = costCache.get(next);
                if (oldCost == null || newCost < oldCost) {
                    costCache.put(next, newCost);
                    double priority = newCost + heuristicSupplier.getHeuristic(start, next, end, edge);
                    open.add(new AStarStore(next, priority));
                    pathCache.put(next, edge);
                }
            }
        }

        if (!debugMode) clear();
        return Optional.empty();
    }

    private List<Edge> collectPath(Node end, Node start) {
        List<Edge> path = new ArrayList<>();
        Edge edge = pathCache.get(end);
        while (edge != null) {
            path.add(edge);
            if (edge.getStart().equals(start)) break;
            edge = pathCache.get(edge.getStart());
        }
        Collections.reverse(path);
        return path;
    }

    private void clear() {
        open.clear();
        costCache.clear();
        pathCache.clear();
    }

    public AStarImplementation setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
        return this;
    }

    public AStarImplementation setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    public AStarImplementation setEdgePredicate(Predicate<Edge> edgePredicate) {
        this.edgePredicate = edgePredicate;
        return this;
    }

    public AStarImplementation setSuccessEvaluator(Function<Node, Boolean> successEvaluator) {
        this.successEvaluator = successEvaluator;
        return this;
    }

    public AStarImplementation setIgnoreStartBlocked(boolean ignoreStartBlocked) {
        this.ignoreStartBlocked = ignoreStartBlocked;
        return this;
    }
}
