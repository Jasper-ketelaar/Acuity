package com.acuitybotting.path_finding.debugging.interactive_map.ui;

import com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective;
import com.acuitybotting.path_finding.debugging.interactive_map.util.ScreenLocation;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.awt.*;

import static com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective.round;

/**
 * Created by Zachary Herridge on 6/18/2018.
 */
public class PaintUtil {

    private MapPanel mapPanel;

    public PaintUtil(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
    }

    private int offSet(int in){
        return round(in - (mapPanel.getPerspective().getTileSize() / 2));
    }

    private Point offset(Point in){
        return new Point(offSet(in.x), offSet(in.y));
    }

    public void markLocation(Graphics2D graphics2D, Object location, Color color){
        ScreenLocation screenLocation = mapPanel.getPerspective().locationToScreen(convertToLocation(location));
        graphics2D.setColor(color);
        graphics2D.fillOval(round(screenLocation.getX()), round(screenLocation.getY()), round(mapPanel.getPerspective().getTileSize()), round(mapPanel.getPerspective().getTileSize()));
    }

    public void connectLocations(Graphics2D graphics2D, Object start, Object end, Color color){
        double tileOffset = mapPanel.getPerspective().getTileSize() / 2;

        ScreenLocation startPoint = mapPanel.getPerspective().locationToScreen(convertToLocation(start));
        ScreenLocation endPoint = mapPanel.getPerspective().locationToScreen(convertToLocation(end));

        graphics2D.setColor(color);
        graphics2D.drawLine(
                round(startPoint.getX() + tileOffset),
                round(startPoint.getY() + tileOffset),
                round(endPoint.getX() + tileOffset),
                round(endPoint.getY() + tileOffset)
        );
    }

    public void fillArea(Graphics2D graphics2D, Object base, int tileWidth, int tileHeight, Color color) {
        double tileOffset = mapPanel.getPerspective().getTileSize() / 2;

        ScreenLocation point = mapPanel.getPerspective().locationToScreen(convertToLocation(base));
        double tileSize = mapPanel.getPerspective().getTileSize();
        graphics2D.setColor(color);
        graphics2D.drawRect(round(point.getX()), round(point.getY()), round((tileSize * tileWidth)), round((tileHeight * tileSize)));
    }

    private Location convertToLocation(Object object){
        if (object == null) return null;
        if (object instanceof Location) return (Location) object;
        if (object instanceof Locateable) return ((Locateable) object).getLocation();
        throw new RuntimeException(object + " must be instance of Location or Locateable.");
    }
}
