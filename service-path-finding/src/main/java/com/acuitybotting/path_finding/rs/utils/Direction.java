package com.acuitybotting.path_finding.rs.utils;

import java.util.Arrays;

public enum Direction {

    NORTH(2),
    NORTH_EAST(-1),
    EAST(1),
    SOUTH_EAST(-1),
    SOUTH(8),
    SOUTH_WEST(-1),
    WEST(4),
    NORTH_WEST(-1);

    int orientation;

    Direction(int orientation) {
        this.orientation = orientation;
    }

    public int getOrientation() {
        return orientation;
    }

    public Direction inverse() {
        if (this == NORTH) return SOUTH;
        if (this == SOUTH) return NORTH;
        if (this == WEST) return EAST;
        if (this == EAST) return WEST;
        return null;
    }

    public static String toString(int orientation){
        return Arrays.stream(values()).filter(direction -> direction.getOrientation() == orientation).findFirst().map(Enum::name).orElse("");
    }

    public boolean isSameAxis(int orientation) {
        if (orientation == getOrientation()) return true;

        if (this == NORTH && SOUTH.getOrientation() == orientation) return true;
        if (this == SOUTH && NORTH.getOrientation() == orientation) return true;

        if (this == EAST && WEST.getOrientation() == orientation) return true;
        if (this == WEST && EAST.getOrientation() == orientation) return true;

        return false;
    }
}