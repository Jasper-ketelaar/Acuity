package com.acuitybotting.path_finding.rs.utils;

import com.acuitybotting.db.arango.path_finding.repositories.xtea.RegionInfoRepository;
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

    public static int worldToRegionId(int worldX, int worldY) {
        worldX >>>= 6;
        worldY >>>= 6;
        return (worldX << 8) | worldY;
    }
}
