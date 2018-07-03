package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;



import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class HPANode implements Node, Locateable {

    public static final int GROUND = 0;
    public static final int STAIR = 1;
    public static final int CUSTOM = 2;
    public static final int DOOR = 3;

    private List<Edge> edges = new ArrayList<>();
    private Location location;
    private HPARegion hpaRegion;
    private int type = GROUND;

    public HPANode(HPARegion region, Location location) {
        this.location = location;
        this.hpaRegion = region;
    }

    @Override
    public List<Edge> getNeighbors(boolean ignoreSelfBlocked) {
        return edges;
    }

    public void unlink() {
        for (Edge edge : edges) {
            ((HPANode) edge.getEnd()).getEdges().removeIf(edge1 -> edge1.getEnd().equals(this));
        }
        edges.clear();
    }

    public HPAEdge addConnection(HPANode other, int edgeType){
        return addConnection(other, edgeType, 1);
    }

    public HPAEdge addConnection(HPANode other, int edgeType, double cost){
        HPAEdge hpaEdge = new HPAEdge(this, other);
        hpaEdge.setCost(cost);
        hpaEdge.setType(edgeType);
        edges.add(hpaEdge);
        return hpaEdge;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof HPANode)) return false;
        HPANode hpaNode = (HPANode) object;
        return Objects.equals(getLocation(), hpaNode.getLocation()) &&
                Objects.equals(this.getHpaRegion(), this.getHpaRegion());
    }

    public HPANode setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocation(), this.getHpaRegion());
    }
}
