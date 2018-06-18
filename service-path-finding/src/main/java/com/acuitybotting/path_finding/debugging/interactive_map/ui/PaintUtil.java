package com.acuitybotting.path_finding.debugging.interactive_map.ui;

import com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.awt.*;

/**
 * Created by Zachary Herridge on 6/18/2018.
 */
public class PaintUtil {

    private MapPanel mapPanel;

    public PaintUtil(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
    }

    public void markTile(Graphics2D graphics2D, Location location, Color color){
        double scale = mapPanel.getPerspective().getScale();
        Point point = mapPanel.getPerspective().locationToScreen(location);

        graphics2D.setColor(color);
        graphics2D.fillOval(point.x, point.y, Perspective.round(scale * 3), Perspective.round(scale * 3));
    }

}
