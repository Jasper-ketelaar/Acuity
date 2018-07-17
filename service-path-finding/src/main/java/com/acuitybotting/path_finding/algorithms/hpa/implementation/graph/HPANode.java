package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;



import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.EdgeType;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

@Getter
public class HPANode implements Node, Locateable {

    private Set<Edge> hpaEdges = new HashSet<>();
    private Set<Edge> temporaryEdges = new CopyOnWriteArraySet<>();

    @Expose
    private Location location;
    private HPARegion hpaRegion;

    @Expose
    private int type = EdgeType.BASIC;

    public HPANode(HPARegion region, Location location) {
        this.location = location;
        this.hpaRegion = region;
    }

    @Override
    public Set<Edge> getNeighbors(Map<String, Object> args) {
        if (temporaryEdges.size() == 0) return hpaEdges;

        Set<Edge> combined = new HashSet<>(hpaEdges);
        combined.addAll(temporaryEdges);

        return combined;
    }

    public HPAEdge addHpaEdge(HPANode other, int edgeType){
        return addHpaEdge(other, edgeType, 1);
    }

    public HPAEdge addHpaEdge(HPANode other, int edgeType, double cost){
        HPAEdge hpaEdge = new HPAEdge(this, other);
        hpaEdge.setCost(cost);
        hpaEdge.setType(edgeType);
        hpaEdges.add(hpaEdge);
        return hpaEdge;
    }

    public HPANode setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HPANode)) return false;

        HPANode hpaNode = (HPANode) o;

        if (getType() != hpaNode.getType()) return false;
        if (getLocation() != null ? !getLocation().equals(hpaNode.getLocation()) : hpaNode.getLocation() != null)
            return false;
        return getHpaRegion() != null ? getHpaRegion().equals(hpaNode.getHpaRegion()) : hpaNode.getHpaRegion() == null;
    }

    @Override
    public int hashCode() {
        int result = getLocation() != null ? getLocation().hashCode() : 0;
        result = 31 * result + (getHpaRegion() != null ? getHpaRegion().hashCode() : 0);
        result = 31 * result + getType();
        return result;
    }

    @Override
    public String toString() {
        return location.toString();
    }
}
