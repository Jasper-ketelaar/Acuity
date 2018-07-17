package com.acuitybotting.path_finding.rs.custom_edges.requirements.implementations;

import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.service.domain.abstractions.player.RSPlayer;

/**
 * Created by Zachary Herridge on 7/17/2018.
 */
public class PlayerImplementation implements Player {

    private RSPlayer rsPlayer;

    public PlayerImplementation(RSPlayer rsPlayer) {
        this.rsPlayer = rsPlayer;
    }
}
