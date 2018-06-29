package com.acuitybotting.path_finding.rs.utils;

import com.acuitybotting.db.arango.path_finding.domain.SceneEntity;
import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionInfo;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.xtea.domain.Region;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */
@Slf4j
public class RsEnvironment {

    public static final File INFO_BASE = new File("C:\\Users\\zgher\\Desktop\\Map Info");

    public static final int PLANE_PENALTY = 25;
    public static final int CACHE_AREA = 15;

    public static final String[] DOOR_NAMES = new String[]{"Door", "Gate", "Large door", "Castle door", "Gate of War", "Rickety door", "Oozing barrier", "Portal of Death", "Magic guild door", "Prison door", "Barbarian door"};
    public static final String[] DOOR_ACTIONS = new String[]{"OPEN"};
    public static final String[] STAIR_NAMES = new String[]{"Stairs", "Ladder", "Stair"};

    private static int regionImageBase = 0;
    private static File[] regionImageBases = new File[]{new File(INFO_BASE, "\\img\\a2_regions\\"), new File(INFO_BASE, "\\img\\regions\\"), new File(INFO_BASE, "\\img\\a_regions\\")};

    private static RsMapService rsMapService;

    private static Map<String, RegionInfo> regionMap = new HashMap<>();
    private static Map<String, BufferedImage> regionImageMap = new HashMap<>();

    public static TileNode getNode(Location location) {
        return new TileNode(location);
    }

    public static void loadRegions(){
        regionMap.clear();
        log.info("Starting region load.");
        for (RegionInfo regionInfo : rsMapService.getRegionInfoRepository().findAll()) {
            regionMap.put(regionInfo.getKey(), regionInfo);
        }
        log.info("Finished region load with {} regions.", regionMap.size());
    }

    private static RegionInfo getRegion(Location location){
        return regionMap.get(String.valueOf(RsMapService.worldToRegionId(location.getX(), location.getY())));
    }

    public static Integer getFlagAt(Location location) {
        RegionInfo regionInfo = getRegion(location);
        if (regionInfo == null) {
            return null;
        }
        int localX = location.getX() - regionInfo.getBaseX();
        int localY = location.getY() - regionInfo.getBaseY();
        return regionInfo.getFlags()[location.getPlane()][localX][localY];
    }

    public static Map<String, RegionInfo> getRegionMap() {
        return regionMap;
    }

    public static Iterable<SceneEntity> getStairsWithin(HPARegion region) {
        return Collections.emptyList();
    }

    public static List<SceneEntity> getDoorsAt(Location location) {
        return Collections.emptyList();
    }

    public static RsMapService getRsMapService() {
        return rsMapService;
    }

    public static void setRsMapService(RsMapService rsMapService) {
        RsEnvironment.rsMapService = rsMapService;
    }

    public static BufferedImage getRegionImage(int regionId, int plane) {
        return regionImageMap.computeIfAbsent(String.valueOf(regionId + "_" + plane), s -> {
            try {
                File file = new File(getRegionImageBase(), + regionId + "_" + plane + ".png");
                return file.exists() ? ImageIO.read(file) : null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static File getRegionImageBase() {
        return regionImageBases[regionImageBase];
    }

    public static int getRegionImageBaseIndex() {
        return regionImageBase;
    }

    public static void setRegionImageBaseIndex(int regionImageBase) {
        RsEnvironment.regionImageBase = regionImageBase;
        regionImageMap.clear();
    }

    public static BufferedImage getRegionImage(Location location, int plane) {
        return getRegionImage(RsMapService.worldToRegionId(location.getX(), location.getY()), plane);
    }
}
