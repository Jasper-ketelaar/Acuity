package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;



import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.Region;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.ArrayList;
import java.util.List;

public class HPANode implements Node, Locateable {

    private List<Edge> edges = new ArrayList<>();
    private Location location;
    private Region region;

    public HPANode(Location location) {
        this.location = location;
    }

    @Override
    public List<Edge> getNeighbors() {
        return edges;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public HPANode setRegion(Region region) {
        this.region = region;
        return this;
    }

    public Region getRegion() {
        return region;
    }
}
