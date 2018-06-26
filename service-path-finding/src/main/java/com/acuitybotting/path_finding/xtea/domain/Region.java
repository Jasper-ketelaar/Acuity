package com.acuitybotting.path_finding.xtea.domain;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 6/25/2018.
 */

@Getter
public class Region {

    public static final int X = 64;
    public static final int Y = 64;
    public static final int Z = 4;

    private int regionID;
    private int baseX;
    private int baseY;

    private int[][][] tileHeights = new int[Z][X][Y];
    private int[][][] tileSettings = new int[Z][X][Y];
    private int[][][] overlayIds = new int[Z][X][Y];
    private int[][][] overlayPaths = new int[Z][X][Y];
    private int[][][] overlayRotations = new int[Z][X][Y];
    private int[][][] underlayIds = new int[Z][X][Y];

    private List<SceneEntityInstance> locations = new ArrayList<>();
    private int[] keys;

    public int getTileSetting(Location location){
        Location clone = location.clone(-baseX, -baseY);
        return tileSettings[location.getPlane()][clone.getX()][clone.getY()];
    }

    public List<SceneEntityInstance> getInstancesAt(Location location){
        return locations.stream().filter(sceneEntityInstance -> sceneEntityInstance.getPosition().toLocation().equals(location)).collect(Collectors.toList());
    }

}
