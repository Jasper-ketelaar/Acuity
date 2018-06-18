package com.acuitybotting.path_finding.debugging.interactive_map.ui;

import com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective;
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

    public void markLocation(Graphics2D graphics2D, Object location, Color color){
        Point point = mapPanel.getPerspective().locationToScreen(convertToLocation(location));

        graphics2D.setColor(color);
        graphics2D.fillOval(point.x, point.y, round(mapPanel.getPerspective().getTileSize()), round(mapPanel.getPerspective().getTileSize()));
    }

    public void connectLocations(Graphics2D graphics2D, Object start, Object end, Color color){
        double tileSize = mapPanel.getPerspective().getTileSize() / 2;
        Point startPoint = mapPanel.getPerspective().locationToScreen(convertToLocation(start));
        Point endPoint = mapPanel.getPerspective().locationToScreen(convertToLocation(end));

        graphics2D.setColor(color);
        graphics2D.drawLine(
                round(startPoint.x + tileSize),
                round(startPoint.y + tileSize),
                round(endPoint.x + tileSize),
                round(endPoint.y + tileSize));
    }

    public void fillArea(Graphics2D graphics2D, Object base, int tileWidth, int tileHeight, Color color) {
        Point point = mapPanel.getPerspective().locationToScreen(convertToLocation(base));

        double tileSize = mapPanel.getPerspective().getTileSize();

        graphics2D.setColor(color);
        graphics2D.drawRect(point.x, point.y, round(tileSize * tileWidth), round(tileHeight * tileSize));
    }

    private Location convertToLocation(Object object){
        if (object == null) return null;
        if (object instanceof Location) return (Location) object;
        if (object instanceof Locateable) return ((Locateable) object).getLocation();
        throw new RuntimeException(object + " must be instance of Location or Locateable.");
    }
}
