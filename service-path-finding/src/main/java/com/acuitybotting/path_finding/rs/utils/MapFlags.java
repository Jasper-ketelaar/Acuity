package com.acuitybotting.path_finding.rs.utils;

import java.lang.reflect.Field;
import java.util.StringJoiner;

/**
 * Created by Zachary Herridge on 6/28/2018.
 */
public class MapFlags {

    public static final int OPEN_SETTINGS = 1;

    public static final int WALL_NORTH = 1 << 1;
    public static final int WALL_EAST = 1 << 2;
    public static final int WALL_SOUTH = 1 << 3;
    public static final int WALL_WEST = 1 << 4;
    public static final int WALL_TYPE_1 = 1 << 17;

    public static final int WALL_NORTH_EAST_TO_SOUTH_WEST = 1 << 5;
    public static final int WALL_NORTH_WEST_TO_SOUTH_EAST = 1 << 6;

    public static final int BLOCKED_SETTING = 1 << 7;
    public static final int BLOCKED_ROOF = 1 << 8;
    public static final int BLOCKED_SCENE_OBJECT = 1 << 16;
    public static final int BLOCKED_22 = 1 << 11;

    public static final int DOOR_FLAG = 1 << 9;

    public static final int OCCUPIED = 1 << 10;

    public static final int PILLAR_NORTH_WEST = 1 << 12;
    public static final int PILLAR_NORTH_EAST = 1 << 13;
    public static final int PILLAR_SOUTH_WEST = 1 << 14;
    public static final int PILLAR_SOUTH_EAST = 1 << 15;

    public static final int OPEN_OVERRIDE = 1 << 21;
    public static final int OPEN_OVERRIDE_START = 1 << 18;
    public static final int OPEN_OVERRIDE_END = 1 << 20;

    public static final int NO_OVERLAY = 1 << 19;

    public static final int PLANE_CHANGE_UP = 1 << 22;
    public static final int PLANE_CHANGE_DOWN = 1 << 23;

    public static int add(int flag, int value){
        return flag | value;
    }

    public static int remove(int flag, int value){
        return flag & value;
    }

    public static boolean check(int flag, int value){
        return (flag & value) != 0;
    }

    public static boolean isBlocked(int flag){
        return check(flag, BLOCKED_22 | BLOCKED_SETTING | BLOCKED_ROOF | BLOCKED_SCENE_OBJECT);
    }

    public static String toString(int flag){
        StringJoiner result = new StringJoiner(" ");

        for (Field field : MapFlags.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                int checkFlag = (int) field.get(null);
                if (check(flag, checkFlag)){
                    result.add(field.getName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(toString(5121));
    }
}
