package com.acuitybotting.path_finding.debugging.interactive_map;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;

import java.awt.*;

@Getter
public class Perspective {

    private GameMap gameMap;
    private MapRenderer mapRenderer;
    private Location base;

    public Perspective(GameMap gameMap, MapRenderer mapRenderer) {
        this.gameMap = gameMap;
        this.base = new Location(3138, 3384 + (600 / 3), 0);
        this.mapRenderer = mapRenderer;
    }

    public Point locationToScreen(Location location){
        Point basePoint = locationToMap(base);
        Point locationPoint = locationToMap(location);
        return new Point(locationPoint.x - basePoint.x, locationPoint.y - basePoint.y);
    }

    public Point locationToMap(Location location){
        Location offset = new Location(location.getX() - gameMap.getBase().getX(), gameMap.getBase().getY() - location.getY(), location.getPlane());
        return new Point(round(offset.getX() * gameMap.getTilePixelSize()), round(offset.getY() * gameMap.getTilePixelSize()));
    }

    public double getTileSize(){
        return gameMap.getTilePixelSize();
    }

    public double getTileWidth(){
        return mapRenderer.getWidth() / getTileSize();
    }

    public double getTileHeight(){
        return mapRenderer.getHeight() / getTileSize();
    }

    private static int round(double value){
        return (int) Math.round(value);
    }
}
