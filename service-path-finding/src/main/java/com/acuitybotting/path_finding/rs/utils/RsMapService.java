package com.acuitybotting.path_finding.rs.utils;

import com.acuitybotting.db.arango.path_finding.repositories.xtea.RegionInfoRepository;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */

@Service
@Getter
public class RsMapService {

    private final RegionInfoRepository regionInfoRepository;

    public RsMapService(RegionInfoRepository regionInfoRepository) {
        this.regionInfoRepository = regionInfoRepository;
    }

    public static int worldToRegionId(Location location) {
        if (location == null) return 0;
        return worldToRegionId(location.getX(), location.getY());
    }

    public static int worldToRegionId(int worldX, int worldY) {
        worldX >>>= 6;
        worldY >>>= 6;
        return (worldX << 8) | worldY;
    }

    public static Location regionIdToBase(int regionId){
        return new Location(((regionId >> 8) & 0xFF) << 6, (regionId & 0xFF) << 6, 0);
    }

    public static Location locationToRegionBase(Location location){
        if (location == null) return null;
        int regionId = worldToRegionId(location.getX(), location.getY());
        return regionIdToBase(regionId);
    }
}
