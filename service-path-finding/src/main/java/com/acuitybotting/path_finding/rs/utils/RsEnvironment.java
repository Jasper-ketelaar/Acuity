package com.acuitybotting.path_finding.rs.utils;

import com.acuitybotting.db.arango.path_finding.domain.SceneEntity;
import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */
public class RsEnvironment {

    public static final int PLANE_PENALTY = 25;
    public static final int CACHE_AREA = 15;

    public static final String[] DOOR_NAMES = new String[]{"Door", "Gate", "Large door", "Castle door", "Gate of War", "Rickety door", "Oozing barrier", "Portal of Death", "Magic guild door", "Prison door", "Barbarian door"};

    private static RsMapService rsMapService;
    private static HashMap<Location, Integer> flagCache = new HashMap<>();

    public static TileNode getNode(Location location) {
        return rsMapService.getNode(location);
    }

    public static TileNode getNode(Location location, int type) {
        return rsMapService.getNode(location, type);
    }

    public static Integer getFlagAt(Location location) {
        Integer flag = flagCache.get(location);
        if (flag != null) return flag;

        Iterable<TileFlag> flags = rsMapService.getTileFlagRepository().findAllByXBetweenAndYBetweenAndPlane(
                location.getX() - CACHE_AREA,
                location.getX() + CACHE_AREA,
                location.getY() - CACHE_AREA,
                location.getY() + CACHE_AREA,
                location.getPlane()
        );

        for (TileFlag tileFlag : flags) {
            flagCache.put(tileFlag.getLocation(), tileFlag.getFlag());
        }

        return flagCache.getOrDefault(location, CollisionFlags.BLOCKED);
    }

    public static List<SceneEntity> getDoorsAt(Location location) {
        return rsMapService.getDoorsAt(location);
    }

    public static RsMapService getRsMapService() {
        return rsMapService;
    }

    public static void setRsMapService(RsMapService rsMapService) {
        RsEnvironment.rsMapService = rsMapService;
    }
}
