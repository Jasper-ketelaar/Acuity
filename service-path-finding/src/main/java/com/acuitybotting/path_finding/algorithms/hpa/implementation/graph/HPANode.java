package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;



import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

@Getter
public class HPANode implements Node, Locateable {

    public static final int GROUND = 0;
    public static final int STAIR = 1;
    public static final int CUSTOM = 2;
    public static final int DOOR = 3;

    private Set<Edge> hpaEdges = new HashSet<>();
    private Set<Edge> temporaryEdges = new CopyOnWriteArraySet<>();

    @Expose
    private Location location;
    private HPARegion hpaRegion;

    @Expose
    private int type = GROUND;

    public HPANode(HPARegion region, Location location) {
        this.location = location;
        this.hpaRegion = region;
    }

    @Override
    public Set<Edge> getNeighbors(boolean ignoreSelfBlocked) {
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
