package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.RsMapService;

import java.awt.*;

public class PositionPlugin extends Plugin {

    private Point start = new Point(10, 10);

    @Override
    public void onPaint(Graphics2D graphics, Graphics2D scaledGraphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(new Color(0, 0, 0, 200));
        graphics.fillRect(start.x, start.y, 300, 100);
        graphics.setColor(Color.WHITE);
        graphics.drawString("Cursor (Map): " + getPerspective().screenToLocation(this.getMapPanel().getMousePosition()), start.x + 20, start.y + 20);
        graphics.drawString("Cursor: " + this.getMapPanel().getMousePosition(), start.x + 20, start.y + 35);
        graphics.drawString("View Base: " + getPerspective().getBase(), start.x + 20, start.y + 50);
        graphics.drawString("Region: " + RsMapService.worldToRegionId(getPerspective().screenToLocation(this.getMapPanel().getMousePosition())), start.x + 20, start.y + 65);


        Location regionBase = RsMapService.locationToRegionBase(getPerspective().screenToLocation(this.getMapPanel().getMousePosition()));
        graphics.drawString("Region Base: " + regionBase, start.x + 20, start.y + 80);
        if (regionBase != null){
            getPaintUtil().markLocation(scaledGraphics, regionBase, Color.RED);
            getPaintUtil().markLocation(scaledGraphics, regionBase.clone(0, 64), Color.GREEN);
        }


    }


    public void onLoad() {
    }

    public void onClose() {
    }
}

