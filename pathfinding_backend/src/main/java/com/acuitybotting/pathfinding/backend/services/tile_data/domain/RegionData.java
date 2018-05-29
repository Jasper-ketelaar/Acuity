package com.acuitybotting.pathfinding.backend.services.tile_data.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
@Getter
@Setter
public class RegionData {

    private int x, y, z;
    private int width, height;

    public Location getBase(){
        return new Location(x, y, z);
    }
}
