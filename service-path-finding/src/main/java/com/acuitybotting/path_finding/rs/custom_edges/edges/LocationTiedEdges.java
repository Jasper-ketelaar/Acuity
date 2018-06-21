package com.acuitybotting.path_finding.rs.custom_edges.edges;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdge;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public class LocationTiedEdges {

    private static Collection<CustomEdge> customEdges = new HashSet<>();

    static {

    }

    public static Collection<CustomEdge> getEdges(){
        return customEdges;
    }
}
