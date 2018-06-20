package com.acuitybotting.path_finding.rs.utils;

import com.acuitybotting.db.arango.path_finding.domain.SceneEntity;
import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.Collections;
import java.util.List;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */
public class RsEnvironment {

    public static final int PLANE_PENALTY = 25;
    public static final int CACHE_AREA = 15;

    public static final String[] DOOR_NAMES = new String[]{"Door", "Gate", "Large door", "Castle door", "Gate of War", "Rickety door", "Oozing barrier", "Portal of Death", "Magic guild door", "Prison door", "Barbarian door"};

    private static RsMapService rsMapService;

    private static LocationBasedCache<Integer> flagCache = new LocationBasedCache<>();
    private static LocationBasedCache<List<SceneEntity>> doorCache = new LocationBasedCache<>();

    public static TileNode getNode(Location location) {
        return rsMapService.getNode(location);
    }

    public static Integer getFlagAt(Location location) {
        Integer flag = flagCache.get(location);
        if (flag != null) return flag;


            flag = flagCache.get(location);
            if (flag != null) return flag;

            Iterable<TileFlag> flags = rsMapService.getTileFlagRepository().findAllByXBetweenAndYBetweenAndPlane(
                    location.getX() - CACHE_AREA,
                    location.getX() + CACHE_AREA,
                    location.getY() - CACHE_AREA,
                    location.getY() + CACHE_AREA,
                    location.getPlane()
            );

            flagCache.fill(  location.getX() - CACHE_AREA,
                    location.getX() + CACHE_AREA,
                    location.getY() - CACHE_AREA,
                    location.getY() + CACHE_AREA,
                    location.getPlane(),
                    CollisionFlags.BLOCKED);

            for (TileFlag tileFlag : flags) {
                flagCache.put(tileFlag.getLocation(), tileFlag.getFlag());
            }

            return flagCache.getOrDefault(location, CollisionFlags.BLOCKED);

    }

    public static List<SceneEntity> getDoorsAt(Location location) {
        List<SceneEntity> sceneEntities = doorCache.get(location);
        if (sceneEntities != null) return sceneEntities;

            sceneEntities = doorCache.get(location);
            if (sceneEntities != null) return sceneEntities;

            Iterable<SceneEntity> doors = rsMapService.getSceneEntityRepository().findAllByXBetweenAndYBetweenAndPlaneAndNameIn(
                    location.getX() - CACHE_AREA,
                    location.getX() + CACHE_AREA,
                    location.getY() - CACHE_AREA,
                    location.getY() + CACHE_AREA,
                    location.getPlane(),
                    DOOR_NAMES
            );

            doorCache.fill(  location.getX() - CACHE_AREA,
                    location.getX() + CACHE_AREA,
                    location.getY() - CACHE_AREA,
                    location.getY() + CACHE_AREA,
                    location.getPlane(),
                    Collections.emptyList());

            for (SceneEntity door : doors) {
                doorCache.put(location, Collections.singletonList(door));
            }

            return doorCache.getOrDefault(location, Collections.emptyList());

    }

    public static RsMapService getRsMapService() {
        return rsMapService;
    }

    public static void setRsMapService(RsMapService rsMapService) {
        RsEnvironment.rsMapService = rsMapService;
    }
}
