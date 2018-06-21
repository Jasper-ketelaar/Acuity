package com.acuitybotting.path_finding.rs.custom_edges.edges;

import com.acuitybotting.path_finding.rs.custom_edges.CustomEdge;
import com.acuitybotting.path_finding.rs.custom_edges.interaction.Interaction;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.PlayerPredicate;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
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
                        .withRequirement((PlayerPredicate) Player::isModernSpellbook)
        );

        customEdges.add(
                new CustomEdge()
                        .setEnd(new Location(3212, 3424, 0))
                        .setInteraction(new Interaction(Interaction.SPELL).setSpellName("VARROCK_TELEPORT"))
                        .withRequirement((PlayerPredicate) Player::isModernSpellbook)
        );

        customEdges.add(
                new CustomEdge()
                        .setEnd(new Location(2757, 3480, 0))
                        .setInteraction(new Interaction(Interaction.SPELL).setSpellName("CAMELOT_TELEPORT"))
                        .withRequirement((PlayerPredicate) Player::isModernSpellbook)
        );
    }

    public static Collection<CustomEdge> getEdges() {
        return customEdges;
    }
}
