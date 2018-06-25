package com.acuitybotting.path_finding.xtea.domain;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;

/**
 * Created by Zachary Herridge on 6/25/2018.
 */
@Getter
public class EntityLocation {

    private int x;
    private int y;
    private int z;

    public Location toLocation(){
        return new Location(x, y, z);
    }
}
