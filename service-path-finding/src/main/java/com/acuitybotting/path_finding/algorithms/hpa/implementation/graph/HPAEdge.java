package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;


import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.PlayerPredicate;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
public class HPAEdge implements Edge {

    @Expose
    private HPANode start, end;

    @Expose
    private double cost;

    @Expose
    private int type;

    @Expose
    private String pathKey;

    private Collection<PlayerPredicate> playerPredicates;
    private Player player;

    public HPAEdge(HPANode start, HPANode end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean evaluate() {
        if (playerPredicates == null) return true;
        for (PlayerPredicate playerPredicate : playerPredicates) {
            if (!playerPredicate.test(player)) return false;
        }
        return true;
    }

    public boolean isInternal(){
        return Objects.equals(start.getHpaRegion(), end.getHpaRegion());
    }

    @Override
    public double getCostPenalty() {
        return cost;
    }

    public HPAEdge setPathKey(String pathKey) {
        this.pathKey = pathKey;
        return this;
    }

    public HPAEdge setCost(double cost) {
        this.cost = cost;
        return this;
    }

    public HPAEdge setType(int type) {
        this.type = type;
        return this;
    }

    public void setPredicates(Player player, Collection<PlayerPredicate> playerPredicates) {
        this.player = player;
        this.playerPredicates = playerPredicates;
    }

    public List<Location> getPath() {
        return RsEnvironment.getRsMap().getPath(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HPAEdge)) return false;

        HPAEdge hpaEdge = (HPAEdge) o;

        if (Double.compare(hpaEdge.getCost(), getCost()) != 0) return false;
        if (getType() != hpaEdge.getType()) return false;
        if (getStart() != null ? !getStart().equals(hpaEdge.getStart()) : hpaEdge.getStart() != null) return false;
        if (getEnd() != null ? !getEnd().equals(hpaEdge.getEnd()) : hpaEdge.getEnd() != null) return false;
        return getPathKey() != null ? getPathKey().equals(hpaEdge.getPathKey()) : hpaEdge.getPathKey() == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getStart() != null ? getStart().hashCode() : 0;
        result = 31 * result + (getEnd() != null ? getEnd().hashCode() : 0);
        temp = Double.doubleToLongBits(getCost());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getType();
        result = 31 * result + (getPathKey() != null ? getPathKey().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HPAEdge{" +
                "start=" + start +
                ", end=" + end +
                ", type=" + type +
                '}';
    }
}
