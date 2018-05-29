package com.acuitybotting.pathfinding.backend.services.tile_data.domain;

import com.acuitybotting.pathfinding.backend.services.tile_data.utils.Regions;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
@Data
@AllArgsConstructor
public class Location {

    private int x, y, z;

    public Location getRegionBase(){
        return new Location(Regions.cordToRegion(x), Regions.cordToRegion(y), z);
    }

    public String toKey() {
        return x + "_" + y + "_" + z;
    }
}
