package com.acuitybotting.path_finding.rs.custom_edges.edges;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdge;
import com.acuitybotting.path_finding.rs.custom_edges.interaction.Interaction;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.PlayerGeneralPredicate;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.PlayerGeneralInfo;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public class PlayerTiedEdges {

    private static Collection<CustomEdge> customEdges = new HashSet<>();

    static {
        customEdges.add(
                new CustomEdge()
                        .setEnd(new Location(3221, 3218, 0))
                        .setInteraction(new Interaction(Interaction.SPELL).setSpellName("HOME_TELEPORT"))
                        .withRequirement((PlayerGeneralPredicate) PlayerGeneralInfo::isModernSpellbook)
        );

        customEdges.add(
                new CustomEdge()
                        .setEnd(new Location(3212, 3424, 0))
                        .setInteraction(new Interaction(Interaction.SPELL).setSpellName("VARROCK_TELEPORT"))
                        .withRequirement((PlayerGeneralPredicate) PlayerGeneralInfo::isModernSpellbook)
        );

        customEdges.add(
                new CustomEdge()
                        .setEnd(new Location(2757, 3480, 0))
                        .setInteraction(new Interaction(Interaction.SPELL).setSpellName("CAMELOT_TELEPORT"))
                        .withRequirement((PlayerGeneralPredicate) PlayerGeneralInfo::isModernSpellbook)
        );
    }

    public static Collection<CustomEdge> getEdges() {
        return customEdges;
    }
}
