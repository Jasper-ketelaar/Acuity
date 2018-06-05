package com.acuitybotting.path_finding.rs.utils;

import java.util.StringJoiner;

public class CollisionFlags {

    public static final int OPEN = 0;

    public static final int BLOCKED_NORTH_WALL  = 1024;
    public static final int BLOCKED_EAST_WALL   = 4096;
    public static final int BLOCKED_SOUTH_WALL  = 16384;
    public static final int BLOCKED_WEST_WALL   = 65536;

    public static final int SOLID = 131072;
    public static final int BLOCKED = 2097152;
    public static final int CLOSED  = 16777215;
    public static final int OCCUPIED = 256;
    public static final int UNINITIALIZED = 16777216;
    public static final int INITIALIZED = 16777216;

    public static final int NORTH_WALL = 2;
    public static final int EAST_WALL = 8;
    public static final int SOUTH_WALL = 32;
    public static final int WEST_WALL = 128;

    public static final int NORTH_WEST_WALL = 1;
    public static final int NORTH_EAST_WALL = 4;
    public static final int SOUTH_EAST_WALL = 16;
    public static final int SOUTH_WEST_WALL = 16777216;

    public static boolean isBlocked(int flag){
        return blocked(flag, OCCUPIED | SOLID | BLOCKED | UNINITIALIZED);
    }

    public static boolean checkWalkable(Direction dir, int locationFlag, int goalFlag) {
        if (isBlocked(goalFlag) || isBlocked(locationFlag)) {
            return false;
        }
        switch (dir) {
            case NORTH:
                if (blocked(locationFlag, NORTH_WALL)) return false;
                break;
            case SOUTH:
                if (blocked(locationFlag, SOUTH_WALL)) return false;
                break;
            case WEST:
                if (blocked(locationFlag, WEST_WALL)) return false;
                break;
            case EAST:
                if (blocked(locationFlag, EAST_WALL)) return false;
                break;
            case NORTH_EAST:
                if (blocked(locationFlag, NORTH_EAST_WALL | NORTH_WALL | EAST_WALL) || (blocked(goalFlag, SOUTH_WALL) && blocked(goalFlag, WEST_WALL)))
                    return false;
                break;
            case NORTH_WEST:
                if (blocked(locationFlag, NORTH_WEST_WALL | NORTH_WALL | WEST_WALL) || (blocked(goalFlag, SOUTH_WALL) && blocked(goalFlag, EAST_WALL)))
                    return false;
                break;
            case SOUTH_EAST:
                if (blocked(locationFlag, SOUTH_EAST_WALL | SOUTH_WALL | EAST_WALL) || (blocked(goalFlag, NORTH_WALL) && blocked(goalFlag, WEST_WALL)))
                    return false;
                break;
            case SOUTH_WEST:
                if (blocked(locationFlag, SOUTH_WEST_WALL | SOUTH_WALL | WEST_WALL) || (blocked(goalFlag, NORTH_WALL) && blocked(goalFlag, EAST_WALL)))
                    return false;
                break;
        }
        return true;
    }

    public static boolean check(int flag, int checkFlag){
        return (flag & checkFlag) == checkFlag;
    }

    public static boolean blockedNorth(int collisionData){
        return CollisionFlags.check(collisionData, CollisionFlags.NORTH_WALL) || CollisionFlags.check(collisionData, CollisionFlags.BLOCKED_NORTH_WALL);
    }

    public static boolean blockedEast(int collisionData){
        return CollisionFlags.check(collisionData, CollisionFlags.EAST_WALL) || CollisionFlags.check(collisionData, CollisionFlags.BLOCKED_EAST_WALL);
    }

    public static boolean blockedSouth(int collisionData){
        return CollisionFlags.check(collisionData, CollisionFlags.SOUTH_WALL) || CollisionFlags.check(collisionData, CollisionFlags.BLOCKED_SOUTH_WALL);
    }

    public static boolean blockedWest(int collisionData){
        return CollisionFlags.check(collisionData, CollisionFlags.WEST_WALL) || CollisionFlags.check(collisionData, CollisionFlags.BLOCKED_WEST_WALL);
    }

    public static boolean isInitialized(int collisionData){
        return !(blockedNorth(collisionData) && blockedEast(collisionData) && blockedSouth(collisionData) && blockedWest(collisionData) && !isWalkable(collisionData)) || CollisionFlags.check(collisionData, CollisionFlags.INITIALIZED);
    }

    public static boolean blocked(int flag, int checkFlag) {
        return (flag & checkFlag) != 0;
    }

    public static boolean isWalkable(int collisionData){
        return !(CollisionFlags.check(collisionData, CollisionFlags.OCCUPIED)
                || CollisionFlags.check(collisionData, CollisionFlags.SOLID)
                || CollisionFlags.check(collisionData, CollisionFlags.BLOCKED)
                || CollisionFlags.check(collisionData, CollisionFlags.CLOSED));
    }

    public static int remove(int flag, int change) {
        int newHash = 0;
        if (OCCUPIED != change && blocked(flag, OCCUPIED)) newHash = newHash | OCCUPIED;
        if (SOLID != change && blocked(flag, SOLID)) newHash = newHash | SOLID;
        if (BLOCKED != change && blocked(flag, BLOCKED)) newHash = newHash | BLOCKED;
        if (OCCUPIED != change && blocked(flag, OCCUPIED)) newHash = newHash | OCCUPIED;
        if (WEST_WALL != change && blocked(flag, WEST_WALL)) newHash = newHash | WEST_WALL;
        if (SOUTH_WEST_WALL != change && blocked(flag, SOUTH_WEST_WALL)) newHash = newHash | SOUTH_WEST_WALL;
        if (SOUTH_WALL != change && blocked(flag, SOUTH_WALL)) newHash = newHash | SOUTH_WALL;
        if (SOUTH_EAST_WALL != change && blocked(flag, SOUTH_EAST_WALL)) newHash = newHash | SOUTH_EAST_WALL;
        if (EAST_WALL != change && blocked(flag, EAST_WALL)) newHash = newHash | EAST_WALL;
        if (NORTH_EAST_WALL != change && blocked(flag, NORTH_EAST_WALL)) newHash = newHash | NORTH_EAST_WALL;
        if (NORTH_WALL != change && blocked(flag, NORTH_WALL)) newHash = newHash | NORTH_WALL;
        if (NORTH_WEST_WALL != change && blocked(flag, NORTH_WEST_WALL)) newHash = newHash | NORTH_WEST_WALL;
        return newHash;
    }

    public static int add(int flag, int change) {
        int newHash = change;
        if (blocked(flag, OCCUPIED)) newHash = newHash | OCCUPIED;
        if (blocked(flag, SOLID)) newHash = newHash | SOLID;
        if (blocked(flag, BLOCKED)) newHash = newHash | BLOCKED;
        if (blocked(flag, WEST_WALL)) newHash = newHash | WEST_WALL;
        if (blocked(flag, SOUTH_WEST_WALL)) newHash = newHash | SOUTH_WEST_WALL;
        if (blocked(flag, SOUTH_WALL)) newHash = newHash | SOUTH_WALL;
        if (blocked(flag, SOUTH_EAST_WALL)) newHash = newHash | SOUTH_EAST_WALL;
        if (blocked(flag, EAST_WALL)) newHash = newHash | EAST_WALL;
        if (blocked(flag, NORTH_EAST_WALL)) newHash = newHash | NORTH_EAST_WALL;
        if (blocked(flag, NORTH_WALL)) newHash = newHash | NORTH_WALL;
        if (blocked(flag, NORTH_WEST_WALL)) newHash = newHash | NORTH_WEST_WALL;
        return newHash;
    }

    public static String toString(int flag) {
        StringJoiner builder = new StringJoiner(", ");
        if (blocked(flag, OCCUPIED)) builder.add("OCCUPIED");
        if (blocked(flag, SOLID)) builder.add("SOLID");
        if (blocked(flag, BLOCKED)) builder.add("BLOCKED");
        if (blocked(flag, WEST_WALL)) builder.add("WEST_WALL");
        if (blocked(flag, SOUTH_WEST_WALL)) builder.add("SOUTH_WEST_WALL");
        if (blocked(flag, SOUTH_WALL)) builder.add("SOUTH_WALL");
        if (blocked(flag, SOUTH_EAST_WALL)) builder.add("SOUTH_EAST_WALL");
        if (blocked(flag, EAST_WALL)) builder.add("EAST_WALL");
        if (blocked(flag, NORTH_EAST_WALL)) builder.add("NORTH_EAST_WALL");
        if (blocked(flag, NORTH_WALL)) builder.add("NORTH_WALL");
        if (blocked(flag, NORTH_WEST_WALL)) builder.add("NORTH_WEST_WALL");
        return builder.toString();
    }
}