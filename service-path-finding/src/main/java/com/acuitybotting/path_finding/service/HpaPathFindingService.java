package com.acuitybotting.path_finding.service;

import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.TerminatingNode;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.PlayerPredicate;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.implementation.PlayerImplementation;
import com.acuitybotting.path_finding.rs.domain.graph.TileEdge;
import com.acuitybotting.path_finding.rs.domain.location.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.EdgeType;
import com.acuitybotting.path_finding.service.domain.PathResult;
import com.acuitybotting.path_finding.service.domain.abstractions.player.RSPlayer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Service
public class HpaPathFindingService {

    private HPAGraph graph;

    private boolean evaluateCustomEdge(HPAEdge hpaEdge, RSPlayer rsPlayer){
        Player player = new PlayerImplementation(rsPlayer);
        Collection<PlayerPredicate> playerPredicates = hpaEdge.getCustomEdgeData().getPlayerPredicates();
        if (playerPredicates != null){
            for (PlayerPredicate playerPredicate : playerPredicates) {
                if (!playerPredicate.test(player)) return false;
            }
        }
        return true;
    }

    public PathResult findPath(Location startLocation, Location endLocation, RSPlayer rsPlayer) throws Exception {
        HPARegion startRegion = graph.getRegionContaining(startLocation);
        HPARegion endRegion = graph.getRegionContaining(endLocation);

        Objects.requireNonNull(startRegion);
        Objects.requireNonNull(endRegion);

        PathResult pathResult = new PathResult();
        if (startRegion.equals(endRegion)) {
            List<TileEdge> internalPath = graph.findInternalPath(startLocation, endLocation, startRegion, true);
            if (internalPath != null) {
                pathResult.setPath(internalPath);
                return pathResult;
            }
        }

        TerminatingNode endNode = new TerminatingNode(endRegion, endLocation);
        endNode.connectToGraph();

        TerminatingNode startNode = new TerminatingNode(startRegion, startLocation).addStartEdges();
        //todo startNode.addStartEdges();
        startNode.connectToGraph();

        try {
            AStarImplementation aStarImplementation = new AStarImplementation()
                    .setEdgePredicate(edge -> {
                        if (edge instanceof HPAEdge){
                            HPAEdge hpaEdge = (HPAEdge) edge;
                            if (hpaEdge.getType() == EdgeType.CUSTOM && hpaEdge.getCustomEdgeData() != null && !evaluateCustomEdge(hpaEdge, rsPlayer)) return false;
                        }

                        Node end = edge.getEnd();
                        return !(edge instanceof TerminatingNode) || end.equals(endNode);
                    });

            List<? extends Edge> hpaPath = aStarImplementation.findPath(new LocateableHeuristic(), startNode, endNode).orElse(null);
            pathResult.setPath(hpaPath);
            pathResult.setAStarImplementation(aStarImplementation);
            return pathResult;
        }
        finally {
            startNode.disconnectFromGraph();
            endNode.disconnectFromGraph();
        }
    }
}
