package com.acuitybotting.path_finding.rs.custom_edges.edges;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public class LocationTiedEdges {

    private static Collection<CustomEdgeData> customEdgeData = new HashSet<>();

    static {

    }

    public static Collection<CustomEdgeData> getEdges(){
        return customEdgeData;
    }
}
