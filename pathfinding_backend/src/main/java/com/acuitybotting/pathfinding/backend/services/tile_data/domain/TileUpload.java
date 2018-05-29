package com.acuitybotting.pathfinding.backend.services.tile_data.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
@Setter
@Getter
public class TileUpload {

    private int x, y, z;
    private int[][] flags;

    public Location getBase(){
        return new Location(x, y, z);
    }
}
