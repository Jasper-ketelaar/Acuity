package com.acuitybotting.path_finding.algorithms.astar;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;

import java.util.*;
import java.util.function.Predicate;

public class AStar {

    private Predicate<Edge> edgePredicate;
    private Map<Node, Edge> cameFrom = new HashMap<>();
    private Map<Node, Double> costSoFar = new HashMap<>();
    private PriorityQueue<AStarStore> open = new PriorityQueue<>();

    public List<Edge> findPath(AStarHeuristicSupplier heuristicSupplier, Node start, Node end) {
        open.clear();
        costSoFar.clear();
        cameFrom.clear();

        open.add(new AStarStore(start, 0));

        costSoFar.put(start, 0d);

        int attempts = 0;

        while (!open.isEmpty()){
            attempts++;
            AStarStore current = open.poll();

            if (attempts >= 1000000){
                System.out.println("Failed to find  path");
                break;
            }

            if (current.getNode().equals(end)){
                System.out.println("Path found");
                break;
            }

            for (Edge edge : current.getNode().getNeighbors()) {
                if (edgePredicate != null && !edgePredicate.test(edge)) continue;

                Node next = edge.getEnd();

                double newCost = costSoFar.getOrDefault(current.getNode(), 0d)
                        + heuristicSupplier.getHeuristic(start, current.getNode(), next, edge);

                Double oldCost = costSoFar.get(next);
                if (oldCost == null || newCost < oldCost){
                    costSoFar.put(next, newCost);
                    double priority = newCost + heuristicSupplier.getHeuristic(start, next, end, edge);
                    open.add(new AStarStore(next, priority));
                    cameFrom.put(next, edge);
                }
            }
        }

        List<Edge> path = new ArrayList<>();
        Edge edge = cameFrom.get(end);
        while (edge != null){
            path.add(edge);
            if (edge.getStart().equals(start)) break;
            edge = cameFrom.get(edge.getStart());
        }
        Collections.reverse(path);
        return path;
    }

    public AStar setEdgePredicate(Predicate<Edge> edgePredicate) {
        this.edgePredicate = edgePredicate;
        return this;
    }

    private static class AStarStore implements Comparable<AStarStore>{

        private Node node;
        private double priority;

        public AStarStore(Node node, double priority) {
            this.node = node;
            this.priority = priority;
        }

        public Node getNode() {
            return node;
        }

        public double getPriority() {
            return priority;
        }

        @Override
        public int compareTo(AStarStore o) {
            return Double.compare(getPriority(), o.getPriority());
        }
    }
}
