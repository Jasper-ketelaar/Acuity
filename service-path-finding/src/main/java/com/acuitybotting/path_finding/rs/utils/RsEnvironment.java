package com.acuitybotting.path_finding.rs.utils;

import com.acuitybotting.db.arango.path_finding.domain.SceneEntity;
import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */
public class RsEnvironment {

    public static final int PLANE_PENALTY = 25;
    public static final int CACHE_AREA = 15;

    public static final String[] DOOR_NAMES = new String[]{"Door", "Gate", "Large door", "Castle door", "Gate of War", "Rickety door", "Oozing barrier", "Portal of Death", "Magic guild door", "Prison door", "Barbarian door"};
    public static final String[] DOOR_ACTIONS = new String[]{"OPEN"};


    public static final String[] STAIR_NAMES = new String[]{"Stairs", "Ladder", "Stair"};

    private static RsMapService rsMapService;

    private static LocationBasedCache<Integer> flagCache = new LocationBasedCache<>();
    private static LocationBasedCache<List<SceneEntity>> doorCache = new LocationBasedCache<>();

    public static TileNode getNode(Location location) {
        return rsMapService.getNode(location);
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

        flag = flagCache.get(location);
        if (flag != null) return flag;

        flagCache.fill(location.getX() - CACHE_AREA,
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

    public static Iterable<SceneEntity> getStairsWithin(HPARegion region) {
        return rsMapService.getSceneEntityRepository().findAllByXBetweenAndYBetweenAndPlaneAndNameIn(
                region.getRoot().getX(),
                region.getRoot().getX() + region.getWidth(),
                region.getRoot().getY(),
                region.getRoot().getY() + region.getHeight(),
                region.getRoot().getPlane(),
                STAIR_NAMES
        );
    }

    public static List<SceneEntity> getDoorsAt(Location location) {
        return getSceneElementAt(location, DOOR_NAMES, DOOR_ACTIONS, doorCache);
    }

    public static List<SceneEntity> getSceneElementAt(Location location, String[] names, String[] actions, LocationBasedCache<List<SceneEntity>> cache) {
        List<SceneEntity> sceneEntities = cache.get(location);
        if (sceneEntities != null) return sceneEntities;

        Iterable<SceneEntity> entities = rsMapService.getSceneEntityRepository().findAllByXBetweenAndYBetweenAndPlaneAndNameIn(
                location.getX() - CACHE_AREA,
                location.getX() + CACHE_AREA,
                location.getY() - CACHE_AREA,
                location.getY() + CACHE_AREA,
                location.getPlane(),
                names
        );

        sceneEntities = cache.get(location);
        if (sceneEntities != null) return sceneEntities;

        doorCache.fill(location.getX() - CACHE_AREA,
                location.getX() + CACHE_AREA,
                location.getY() - CACHE_AREA,
                location.getY() + CACHE_AREA,
                location.getPlane(),
                Collections.emptyList());

        Iterator<SceneEntity> iterator = entities.iterator();
        if (iterator.hasNext()) {
            SceneEntity next = iterator.next();
            if (actions == null || (next.getActions() != null && Arrays.stream(next.getActions()).anyMatch(s -> Arrays.stream(actions).anyMatch(s1 -> s1.equalsIgnoreCase(s))))) {
                cache.put(location, Collections.singletonList(iterator.next()));
            }
        }

        return cache.getOrDefault(location, Collections.emptyList());
    }

    public static RsMapService getRsMapService() {
        return rsMapService;
    }

    public static void setRsMapService(RsMapService rsMapService) {
        RsEnvironment.rsMapService = rsMapService;
    }
}
