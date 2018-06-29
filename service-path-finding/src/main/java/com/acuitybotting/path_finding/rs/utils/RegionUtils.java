package com.acuitybotting.path_finding.rs.utils;

import com.acuitybotting.path_finding.rs.domain.location.Location;

/**
 * Created by Zachary Herridge on 6/29/2018.
 */
public class RegionUtils {

    public static int locationToRegionId(Location location) {
        if (location == null) return 0;
        return locationToRegionId(location.getX(), location.getY());
    }

    public static int locationToRegionId(int worldX, int worldY) {
        worldX >>>= 6;
        worldY >>>= 6;
        return (worldX << 8) | worldY;
    }

    public static Location regionIdToBase(int regionId){
        return new Location(((regionId >> 8) & 0xFF) << 6, (regionId & 0xFF) << 6, 0);
    }

    public static Location locationToRegionBase(Location location){
        if (location == null) return null;
        int regionId = locationToRegionId(location.getX(), location.getY());
        return regionIdToBase(regionId);
    }
}
