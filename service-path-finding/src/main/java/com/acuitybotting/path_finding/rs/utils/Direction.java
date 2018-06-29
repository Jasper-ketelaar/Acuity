package com.acuitybotting.path_finding.rs.utils;

import java.util.Arrays;

public enum Direction {

    NORTH(0, 1),
    NORTH_EAST(1, 1),
    EAST(1, 0),
    SOUTH_EAST(1, -1),
    SOUTH(0, -1),
    SOUTH_WEST(-1, -1),
    WEST(-1, 0),
    NORTH_WEST(-1, 1);

    int xOff;
    int yOff;

    Direction(int xOff, int yOff) {
        this.xOff = xOff;
        this.yOff = yOff;
    }

    public int getXOff() {
        return xOff;
    }

    public int getYOff() {
        return yOff;
    }
}