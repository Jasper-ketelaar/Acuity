package com.acuitybotting.pathfinding.backend.services.tile_data.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
@Setter
@Getter
@NoArgsConstructor
public class Region {

    private Location base;
    private int[][] flags;

    public Region(Location location) {
        this.base = location;
        flags = new int[10][10];
    }
}
