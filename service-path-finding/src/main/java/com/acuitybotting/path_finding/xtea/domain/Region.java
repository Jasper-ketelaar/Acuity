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
    private byte[][][] tileSettings = new byte[Z][X][Y];
    private byte[][][] overlayIds = new byte[Z][X][Y];
    private byte[][][] overlayPaths = new byte[Z][X][Y];
    private byte[][][] overlayRotations = new byte[Z][X][Y];
    private byte[][][] underlayIds = new byte[Z][X][Y];

    private List<SceneEntity> locations = new ArrayList<>();
    private int[] keys;

}
