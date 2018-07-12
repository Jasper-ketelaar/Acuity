package com.acuitybotting.path_finding.rs.utils;

import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 7/12/2018.
 */
public class PathingSceneEntityUtil {

    private static Set<String> planeChangeNames = new HashSet<>();
    private static Set<Location> doorLocationBlacklist = new HashSet<>();

    static {
        planeChangeNames.add("ladder");
        planeChangeNames.add("stairs");
        planeChangeNames.add("staircase");
        planeChangeNames.add("stairwell");

        blackListDoorLocation(3268, 3227, 0);
        blackListDoorLocation(3268, 3228, 0);
    }

    private static boolean isPlaneChange(String name, String[] actions, Integer objectId){
        if (name == null) return false;
        return planeChangeNames.contains(name.toLowerCase());
    }

    public static boolean isNegativePlaneChange(String name, String[] actions, Integer objectId) {
        if (!isPlaneChange(name, actions, objectId)) return false;
        return getPlaneChangeAction(false, actions).size() > 0;
    }

    public static boolean isPositivePlaneChange(String name, String[] actions, Integer objectId) {
        if (!isPlaneChange(name, actions, objectId)) return false;
        return getPlaneChangeAction(true, actions).size() > 0;
    }

    public static Set<String> getPlaneChangeAction(boolean positiveLevelChange, String[] actions){
        if (positiveLevelChange) return Arrays.stream(actions).filter(s -> s != null && s.toLowerCase().contains("up")).collect(Collectors.toSet());
        return Arrays.stream(actions).filter(s -> s != null && s.toLowerCase().contains("down")).collect(Collectors.toSet());
    }

    public static boolean isPlaneChange(boolean positiveLevelChange, String name, String[] actions, Integer objectId) {
        if (positiveLevelChange) return isPositivePlaneChange(name, actions, objectId);
        else return isNegativePlaneChange(name, actions, objectId);
    }

    public static boolean isDoor(Location position, String name, String[] actions, Integer mapDoorFlag) {
        if (position != null && doorLocationBlacklist.contains(position)) return false;
        return mapDoorFlag != null && mapDoorFlag != 0;
    }

    public static void blackListDoorLocation(int x, int y, int plane){
        doorLocationBlacklist.add(new Location(x, y, plane));
    }
}
