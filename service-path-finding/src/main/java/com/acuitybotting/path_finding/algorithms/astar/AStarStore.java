package com.acuitybotting.path_finding.algorithms.astar;

import com.acuitybotting.path_finding.algorithms.graph.Node;

public class AStarStore implements Comparable<AStarStore>{

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