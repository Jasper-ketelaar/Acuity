package com.acuitybotting.path_finding.rs.utils;

public enum Direction {

    NORTH(2, 0, 1),
    NORTH_EAST(404, 1, 1),
    EAST(1, 1, 0),
    SOUTH_EAST(404, 1, -1),
    SOUTH(8, 0, -1),
    SOUTH_WEST(404, -1, -1),
    WEST(4, -1, 0),
    NORTH_WEST(404, -1, 1);

    int orientation;
    int xOff;
    int yOff;

    Direction(int orientation, int xOff, int yOff) {
        this.xOff = xOff;
        this.yOff = yOff;
    }

    public int getXOff() {
        return xOff;
    }

    public int getYOff() {
        return yOff;
    }

    public int getOrientation() {
        return orientation;
    }
}