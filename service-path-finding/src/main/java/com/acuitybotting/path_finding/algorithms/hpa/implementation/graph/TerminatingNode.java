package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;

import com.acuitybotting.path_finding.rs.domain.location.Location;

/**
 * Created by Zachary Herridge on 6/20/2018.
 */
public class TerminatingNode extends HPANode{

    public TerminatingNode(HPARegion HPARegion, Location location) {
        super(HPARegion, location);
    }
}
