package com.acuitybotting.path_finding.rs.custom_edges;

import com.acuitybotting.path_finding.rs.custom_edges.interaction.Interaction;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.PlayerPredicate;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.google.gson.annotations.Expose;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public class CustomEdgeData {

    @Expose
    private Location start, end;
    private Collection<PlayerPredicate> playerPredicates = new HashSet<>();

    @Expose
    private Interaction interaction;

    public Location getStart() {
        return start;
    }

    public CustomEdgeData setStart(Location start) {
        this.start = start;
        return this;
    }

    public Location getEnd() {
        return end;
    }

    public CustomEdgeData setEnd(Location end) {
        this.end = end;
        return this;
    }

    public CustomEdgeData withRequirement(PlayerPredicate predicate){
        playerPredicates.add(predicate);
        return this;
    }

    public Collection<PlayerPredicate> getPlayerPredicates() {
        return playerPredicates;
    }

    public CustomEdgeData setPlayerPredicates(Collection<PlayerPredicate> playerPredicates) {
        this.playerPredicates = playerPredicates;
        return this;
    }

    public Interaction getInteraction() {
        return interaction;
    }

    public CustomEdgeData setInteraction(Interaction interaction) {
        this.interaction = interaction;
        return this;
    }
}
