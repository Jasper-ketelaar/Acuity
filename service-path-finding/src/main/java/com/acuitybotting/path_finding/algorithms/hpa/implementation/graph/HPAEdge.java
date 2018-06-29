package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;


import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.PlayerPredicate;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
public class HPAEdge implements Edge {

    private HPANode start, end;
    private double cost;
    private int type;

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

    public List<Edge> getPath() {
        return RsEnvironment.getRsMap().getPath(this);
    }
}
