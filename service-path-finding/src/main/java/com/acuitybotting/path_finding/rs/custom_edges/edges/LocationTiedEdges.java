package com.acuitybotting.path_finding.rs.custom_edges.edges;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.custom_edges.interaction.Interaction;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public class LocationTiedEdges {

    private static Collection<CustomEdgeData> customEdgeData = new HashSet<>();

    static {
        add(new CustomEdgeData()
                .setStart(new Location(123, 123, 0))
                .setEnd(new Location(123, 123, 0))
                .setInteraction(new Interaction().setType(Interaction.FAIRY_RING).withData("code", "123123"))
        );
    }

    private static void add(CustomEdgeData edge){
        customEdgeData.add(edge);
    }

    public static Collection<CustomEdgeData> getEdges(){
        return customEdgeData;
    }
}
