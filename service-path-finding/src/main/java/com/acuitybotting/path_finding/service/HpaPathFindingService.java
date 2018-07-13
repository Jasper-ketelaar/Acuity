package com.acuitybotting.path_finding.service;

import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.TerminatingNode;
import com.acuitybotting.path_finding.rs.domain.graph.TileEdge;
import com.acuitybotting.path_finding.rs.domain.location.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Service
public class HpaPathFindingService {

    private HPAGraph graph;

    public List<? extends Edge> findPath(Location startLocation, Location endLocation) throws Exception {
        HPARegion startRegion = graph.getRegionContaining(startLocation);
        HPARegion endRegion = graph.getRegionContaining(endLocation);

        Objects.requireNonNull(startRegion);
        Objects.requireNonNull(endRegion);

        if (startRegion.equals(endRegion)) {
            List<TileEdge> internalPath = graph.findInternalPath(startLocation, endLocation, startRegion, true);
            if (internalPath != null) return internalPath;
        }

        TerminatingNode endNode = new TerminatingNode(endRegion, endLocation);
        endNode.connectToGraph();


        TerminatingNode startNode = new TerminatingNode(startRegion, startLocation).addStartEdges();
        //todo startNode.addStartEdges();
        startNode.connectToGraph();

        try {
            AStarImplementation aStarImplementation = new AStarImplementation()
                    .setEdgePredicate(edge -> {
                        Node end = edge.getEnd();
                        return !(edge instanceof TerminatingNode) || end.equals(endNode);
                    });

            return aStarImplementation.findPath(new LocateableHeuristic(), startNode, endNode).orElse(null);
        }
        finally {
            startNode.disconnectFromGraph();
            endNode.disconnectFromGraph();
        }
    }
}
