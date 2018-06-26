package com.acuitybotting.path_finding.debugging.interactive_map.ui;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective;
import com.acuitybotting.path_finding.debugging.interactive_map.util.ScreenLocation;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import static com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective.round;

/**
 * Created by Zachary Herridge on 6/18/2018.
 */
public class PaintUtil {

    private MapPanel mapPanel;

    private List<String> debugs = new ArrayList<>();
    private Point debugStart = new Point(10, 10);
    private int debugSpacing = 15;

    public PaintUtil(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
    }

    public void markLocation(Graphics2D graphics2D, Object location, Color color){
        ScreenLocation screenLocation = mapPanel.getPerspective().locationToScreen(convertToLocation(location));
        if (screenLocation == null) return;
        graphics2D.setColor(color);
        graphics2D.fillOval(round(screenLocation.getX()), round(screenLocation.getY()), round(mapPanel.getPerspective().getGraphicsTileSize()), round(mapPanel.getPerspective().getGraphicsTileSize()));
    }

    public void connectLocations(Graphics2D graphics2D, Object start, Object end, Color color){
        double tileOffset = mapPanel.getPerspective().getGraphicsTileSize() / 2;

        ScreenLocation startPoint = mapPanel.getPerspective().locationToScreen(convertToLocation(start));
        ScreenLocation endPoint = mapPanel.getPerspective().locationToScreen(convertToLocation(end));

        if (startPoint == null || endPoint == null) return;

        graphics2D.setColor(color);
        graphics2D.drawLine(
                round(startPoint.getX() + tileOffset),
                round(startPoint.getY() + tileOffset),
                round(endPoint.getX() + tileOffset),
                round(endPoint.getY() + tileOffset)
        );
    }

    public void fillArea(Graphics2D graphics2D, Object base, int tileWidth, int tileHeight, Color color) {
        ScreenLocation point = mapPanel.getPerspective().locationToScreen(convertToLocation(base));
        if (point == null) return;
        double tileSize = mapPanel.getPerspective().getGraphicsTileSize();
        graphics2D.setColor(color);
        graphics2D.drawRect(round(point.getX()), round(point.getY()), round((tileSize * tileWidth)), round((tileHeight * tileSize)));
    }

    private Location convertToLocation(Object object){
        if (object == null) return null;
        if (object instanceof Location) return (Location) object;
        if (object instanceof Locateable) return ((Locateable) object).getLocation();
        throw new RuntimeException(object + " must be instance of Location or Locateable.");
    }

    public void connectLocations(Graphics2D graphics, Collection<Edge> neighbors, Color color) {
        if (neighbors == null) return;
        for (Edge neighbor : neighbors) {
            connectLocations(graphics, neighbor.getStart(), neighbor.getEnd(), color);
        }
    }

    public void onPaintStart(Graphics2D graphics){
        debugs.clear();
    }

    public void onPaintEnd(Graphics2D graphics){
        if (debugs.size() == 0) return;

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(new Color(0, 0, 0, 200));
        graphics.fillRect(debugStart.x, debugStart.y, 300, (debugs.size() * 15)  + debugSpacing);
        graphics.setColor(Color.WHITE);

        int off = debugSpacing;
        for (String debug : debugs) {
            graphics.drawString(debug, debugStart.x + 15, debugStart.y + off);
            off += debugSpacing;
        }
    }

    public void debug(String message) {
        debugs.add( message);
    }

    public void forEachTile(Consumer<Location> consumer) {
        Location base = mapPanel.getPerspective().getBase();
        for (int x = 0; x < mapPanel.getPerspective().getTileWidth(); x++) {
            for (int y = 0; y < mapPanel.getPerspective().getTileHeight(); y++) {
                consumer.accept(base.clone(x, -y));
            }
        }
    }
}
