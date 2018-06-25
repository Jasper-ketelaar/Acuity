package com.acuitybotting.path_finding.xtea.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

}
