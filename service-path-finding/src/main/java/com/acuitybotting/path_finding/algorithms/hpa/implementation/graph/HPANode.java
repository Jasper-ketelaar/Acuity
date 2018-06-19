package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;



import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.Region;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class HPANode implements Node, Locateable {

    private List<Edge> edges = new ArrayList<>();
    private Location location;
    private Region region;

    public HPANode(Region region, Location location) {
        this.location = location;
        this.region = region;
    }

    @Override
    public List<Edge> getNeighbors() {
        return edges;
    }

    public HPAEdge addConnection(HPANode other){
        HPAEdge hpaEdge = new HPAEdge(this, other);
        edges.add(hpaEdge);
        return hpaEdge;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof HPANode)) return false;
        HPANode hpaNode = (HPANode) object;
        return Objects.equals(getLocation(), hpaNode.getLocation()) &&
                Objects.equals(getRegion(), hpaNode.getRegion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocation(), getRegion());
    }
}
