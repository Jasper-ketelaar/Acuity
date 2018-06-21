package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdge;
import com.acuitybotting.path_finding.rs.custom_edges.edges.PlayerTiedEdges;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.rs.domain.location.Location;

/**
 * Created by Zachary Herridge on 6/20/2018.
 */
public class TemporaryNode extends HPANode {

    public TemporaryNode(HPARegion region, Location location) {
        super(region, location);
    }

    public TemporaryNode addStartEdges(){
        for (CustomEdge customEdge : PlayerTiedEdges.getEdges()) {
            HPARegion endRegion = getHpaRegion().getHpaGraph().getRegionContaining(customEdge.getEnd());
            if (endRegion == null) continue;
            HPANode endNode = endRegion.getNodes().get(customEdge.getEnd());
            if (endNode == null) continue;

            addConnection(endNode, HPANode.CUSTOM)
                    .setCost(30)
                    .setPredicates(new Player() {}, customEdge.getPlayerPredicates());
        }
        return this;
    }
}
