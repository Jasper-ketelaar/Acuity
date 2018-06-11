package com.acuitybotting.path_finding.rs.utils;

import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */
public class LocationBasedCache<T> {

    private Map<Location, T> cache = new HashMap<>();

    public final Object lock = new Object();

    public void fill(int xLower, int xUpper, int yLower, int yUpper, int plane, T value){
        for (int x = xLower; x < xUpper; x++) {
            for (int y = yLower; y < yUpper; y++) {
                cache.put(new Location(x, y, plane), value);
            }
        }
    }

    public void put(Location location, T value){
        cache.put(location, value);
    }

    public T get(Location location){
        return cache.get(location);
    }

    public T getOrDefault(Location location, T defaultValue) {
        T value = get(location);
        return value == null ? defaultValue : value;
    }
}
