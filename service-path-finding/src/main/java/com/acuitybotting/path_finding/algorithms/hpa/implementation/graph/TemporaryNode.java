package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdge;
import com.acuitybotting.path_finding.rs.custom_edges.edges.PlayerTiedEdges;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.PlayerPredicate;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.rs.domain.location.Location;

/**
 * Created by Zachary Herridge on 6/20/2018.
 */
public class TemporaryNode extends HPANode{

    public TemporaryNode(HPARegion HPARegion, Location location) {
        super(HPARegion, location);
    }

    public TemporaryNode addStartEdges(){
        for (CustomEdge customEdge : PlayerTiedEdges.getEdges()) {
            HPARegion endRegion = getHPARegion().getHpaGraph().getRegionContaining(customEdge.getEnd());
            if (endRegion == null) continue;
            HPANode endNode = endRegion.getNodes().get(customEdge.getEnd());
            if (endNode == null) continue;

            HPAEdge edge = new HPAEdge(this, endNode) {
                @Override
                public boolean evaluate() {
                    for (PlayerPredicate predicate : customEdge.getRequirement()) {
                        if (!predicate.test(new Player() {})) return false;
                    }
                    return true;
                }
            };

            edge.setCost(30);
            getEdges().add(edge);
        }
        return this;
    }
}
