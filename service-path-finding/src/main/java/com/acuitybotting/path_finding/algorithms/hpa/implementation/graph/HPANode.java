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

    private List<Edge> edges = new ArrayList<>();
    private Location location;
    private HPARegion HPARegion;
    private int type = GROUND;

    public HPANode(HPARegion HPARegion, Location location) {
        this.location = location;
        this.HPARegion = HPARegion;
    }

    @Override
    public List<Edge> getNeighbors() {
        return edges;
    }

    public HPAEdge addConnection(HPANode other){
        HPAEdge hpaEdge = new HPAEdge(this, other);
        List<Edge> path = Collections.singletonList(hpaEdge);
        hpaEdge.setCost(path.size());
        hpaEdge.setPath(path);
        edges.add(hpaEdge);
        return hpaEdge;
    }

    public HPAEdge addConnection(HPANode other, List<Edge> path){
        HPAEdge hpaEdge = new HPAEdge(this, other);
        hpaEdge.setCost(path.size());
        hpaEdge.setPath(path);
        edges.add(hpaEdge);
        return hpaEdge;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof HPANode)) return false;
        HPANode hpaNode = (HPANode) object;
        return Objects.equals(getLocation(), hpaNode.getLocation()) &&
                Objects.equals(this.getHPARegion(), this.getHPARegion());
    }

    public HPANode setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocation(), this.getHPARegion());
    }
}
