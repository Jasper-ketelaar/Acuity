package com.acuitybotting.path_finding.service;

import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.TemporaryNode;
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

    public List<Edge> findPath(Location startLocation, Location endLocation) throws Exception {
        HPARegion startRegion = graph.getRegionContaining(startLocation);
        HPARegion endRegion = graph.getRegionContaining(endLocation);

        Objects.requireNonNull(startRegion);
        Objects.requireNonNull(endRegion);

        if (startRegion.equals(endRegion)) {
            List<Edge> internalPath = graph.findInternalPath(startLocation, endLocation, startRegion, true);
            if (internalPath != null) return internalPath;
        }

        TemporaryNode endNode = new TemporaryNode(endRegion, endLocation);
        graph.findInternalConnections(endRegion, endNode, 8);

        TemporaryNode startNode = new TemporaryNode(startRegion, startLocation).addStartEdges();
        graph.findInternalConnections(startRegion, startNode, 8);

        AStarImplementation aStarImplementation = new AStarImplementation()
                .setEdgePredicate(edge -> {
                    Node end = edge.getEnd();
                    return !(edge instanceof TemporaryNode) || end.equals(endNode);
                });

        return aStarImplementation.findPath(new LocateableHeuristic(), startNode, endNode).orElse(null);
    }
}
