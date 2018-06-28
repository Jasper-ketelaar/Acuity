package com.acuitybotting.path_finding.debugging.interactive_map.util;

import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapPanel;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;

import java.awt.*;

@Getter
public class Perspective {


    private MapPanel mapPanel;
    private Location base;

    private int mapTileSize = 4;
    private double graphicsTileSize = 4;
    
    public Perspective(MapPanel mapPanel) {
        this.base = new Location(3138, 3384 + (600 / 3), 0);
        this.mapPanel = mapPanel;
    }

    public Location screenToLocation(Point point){
        if (point == null) return null;
        Location offset = new Location((int) (point.x / getGraphicsTileSize()) , (int) (point.y / getGraphicsTileSize()), base.getPlane());
        return base.clone(offset.getX(), -offset.getY());
    }

    public ScreenLocation locationToScreen(Location location){
        if (location == null) return null;
        if (location.getPlane() != base.getPlane()) return null;
        return new ScreenLocation(locationXToScreen(location.getX()), locationYToScreen(location.getY()));
    }

    private double locationXToScreen(int x){
        return (x - base.getX()) * getGraphicsTileSize();
    }

    private double locationYToScreen(int y){
        return (base.getY() - y) * getGraphicsTileSize();
    }

    public ScreenLocation locationToMap(Location location){
        return new ScreenLocation(locationXToMap(location.getX()), locationYToMap(location.getY()));
    }

    private double locationXToMap(int x){
        return (x - base.getX()) * getMapTileSize();
    }

    private double locationYToMap(int y){
        return ((base.getY() - y) + 1) * getMapTileSize();
    }

    public void incScale(double value){
        graphicsTileSize = Math.max(0.2, graphicsTileSize + value);
    }

    public Location getCenterLocation(){
        return base.clone(round(getTileWidth() / 2), round(getTileHeight() / 2));
    }

    public void centerOn(Location location){
        base = location.clone(-round(getTileWidth() / 2), -round(getTileHeight() / 2));
    }

    public double getTileWidth(){
        return mapPanel.getWidth() / getGraphicsTileSize();
    }

    public double getTileHeight(){
        return mapPanel.getHeight() / getGraphicsTileSize();
    }

    public static int round(double value){
        return (int) Math.round(value);
    }

    public double getScale() {
        return getGraphicsTileSize() / getMapTileSize();
    }
}
