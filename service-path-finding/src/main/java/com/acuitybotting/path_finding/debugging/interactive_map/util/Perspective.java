package com.acuitybotting.path_finding.debugging.interactive_map.util;

import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapPanel;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;

import java.awt.*;

@Getter
public class Perspective {


    private MapPanel mapPanel;
    private Location base;

    private int tilePixelSize = 4;
    private double scale = 1;

    public Perspective(GameMap gameMap, MapPanel mapPanel) {
        this.base = new Location(3138, 3384 + (600 / 3), 0);
        this.mapPanel = mapPanel;
    }

    public ScreenLocation locationToScreen(Location location){
        if (location.getPlane() != base.getPlane()) return null;
        return new ScreenLocation(locationXToScreen(location.getX()), locationYToScreen(location.getY()));
    }

    public Location screenToLocation(Point point){
        if (point == null) return null;
        Location offset = new Location((int) (point.x / getTileSize()) , (int) (point.y / getTileSize()), base.getPlane());
        return base.clone(offset.getX(), -offset.getY());
    }

    private double locationXToScreen(int x){
        return (x - base.getX()) * getTileSize();
    }

    private double locationYToScreen(int y){
        return (base.getY() - y) * getTileSize();
    }

    public void incScale(double value){
        scale = Math.max(0.2, scale + value);
    }

    public double getTileSize(){
        return getTilePixelSize() * scale;
    }

    public Location getCenterLocation(){
        return base.clone(round(getTileWidth() / 2), round(getTileHeight() / 2));
    }

    public void centerOn(Location location){
        base = location.clone(-round(getTileWidth() / 2), -round(getTileHeight() / 2));
    }

    public double getTileWidth(){
        return mapPanel.getWidth() / getTileSize();
    }

    public double getTileHeight(){
        return mapPanel.getHeight() / getTileSize();
    }

    public static int round(double value){
        return (int) Math.round(value);
    }
}
