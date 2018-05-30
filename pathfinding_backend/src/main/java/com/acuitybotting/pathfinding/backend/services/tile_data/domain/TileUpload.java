package com.acuitybotting.pathfinding.backend.services.tile_data.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
@Setter
@Getter
@Builder
public class TileUpload {

    private int x, y, z;
    private int width, height;
    private int[][] flags;

    public Location getBase(){
        return new Location(x, y, z);
    }

    public List<Point> getPolygon() {
        return Stream.of(new Point(x, y), new Point(x + width, y), new Point(x + width, y + height), new Point(x, y + height)).collect(Collectors.toList());
    }
}
