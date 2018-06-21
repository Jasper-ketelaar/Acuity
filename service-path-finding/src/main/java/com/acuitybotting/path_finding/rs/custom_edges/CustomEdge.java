package com.acuitybotting.path_finding.rs.custom_edges;

import com.acuitybotting.path_finding.rs.custom_edges.interaction.Interaction;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public class CustomEdge {

    private Location start, end;
    private Collection<Predicate<?>> requirement = new HashSet<>();
    private Interaction interaction;

    public Location getStart() {
        return start;
    }

    public CustomEdge setStart(Location start) {
        this.start = start;
        return this;
    }

    public Location getEnd() {
        return end;
    }

    public CustomEdge setEnd(Location end) {
        this.end = end;
        return this;
    }

    public CustomEdge withRequirement(Predicate<?> predicate){
        requirement.add(predicate);
        return this;
    }

    public Collection<Predicate<?>> getRequirement() {
        return requirement;
    }

    public CustomEdge setRequirement(Collection<Predicate<?>> requirement) {
        this.requirement = requirement;
        return this;
    }

    public Interaction getInteraction() {
        return interaction;
    }

    public CustomEdge setInteraction(Interaction interaction) {
        this.interaction = interaction;
        return this;
    }
}
