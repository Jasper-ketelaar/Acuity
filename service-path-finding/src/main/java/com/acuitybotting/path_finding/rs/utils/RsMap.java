package com.acuitybotting.path_finding.rs.utils;

import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionMap;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.xtea.domain.RsRegion;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/29/2018.
 */
@Slf4j
@Getter
public class RsMap {

    private Map<Integer, RegionMap> regions = new HashMap<>();

    private Integer lowestX, lowestY, highestX, highestY;

    public Optional<RegionMap> getRegion(int regionId) {
        return Optional.ofNullable(regions.get(regionId));
    }

    public Optional<RegionMap> getRegion(Location location) {
        return Optional.ofNullable(regions.get(RegionUtils.locationToRegionId(location.getX(), location.getY())));
    }

    public Optional<Integer> getFlagAt(Location location) {
        RegionMap regionMap = getRegion(location).orElse(null);
        if (regionMap == null) return Optional.empty();
        int localX = location.getX() - regionMap.getBaseX();
        int localY = location.getY() - regionMap.getBaseY();
        return Optional.of(regionMap.getFlags()[location.getPlane()][localX][localY]);
    }

    public TileNode getNode(Location location) {
        return new TileNode(location);
    }

    public void calculateBounds() {
        for (RegionMap region : regions.values()) {
            if (lowestX == null || region.getBaseX() < lowestX) {
                lowestX = region.getBaseX();
            }

            if (highestX == null || (region.getBaseX() + RsRegion.X) > highestX) {
                highestX = (region.getBaseX() + RsRegion.X);
            }

            if (lowestY == null || region.getBaseY() < lowestY) {
                lowestY = region.getBaseY();
            }

            if (highestY == null || (region.getBaseY() + RsRegion.Y) > highestY) {
                highestY = (region.getBaseY() + RsRegion.Y);
            }
        }

        log.info("Calculated RsMap bound {}, {} to {}, {}.", lowestX, lowestY, highestX, highestY);
    }
}
