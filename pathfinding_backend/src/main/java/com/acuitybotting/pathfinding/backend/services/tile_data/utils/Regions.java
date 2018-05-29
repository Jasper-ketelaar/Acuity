package com.acuitybotting.pathfinding.backend.services.tile_data.utils;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
public class Regions {

    public static int cordToRegion(int cord){
        return cordToRegion(false, cord);
    }

    public static int cordToRegion(boolean formatted, int cord){
        return formatted ? (cord >> 3) - 6 : (cord >> 3);
    }
}
