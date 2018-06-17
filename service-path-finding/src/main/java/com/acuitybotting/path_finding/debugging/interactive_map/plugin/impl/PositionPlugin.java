package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapPanel;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;

import java.awt.*;

public class PositionPlugin extends Plugin {

    private Point start = new Point(10, 10);

    public PositionPlugin(MapPanel mapPanel) {
        super(mapPanel);
    }

    @Override
    public void onPaint(Graphics2D graphics, Graphics2D scaledGraphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(new Color(0, 0, 0, 200));
        graphics.fillRect(start.x, start.y, 300, 100);
        graphics.setColor(Color.WHITE);
        graphics.drawString("Cursor: " + this.getMapPanel().getMousePosition(), start.x + 20, start.y + 35);
        graphics.drawString("Cursor (Map): " + getPerspective().screenToLocation(this.getMapPanel().getMousePosition()), start.x + 20, start.y + 20);
        graphics.drawString("View Base: " + getPerspective().getBase(), start.x + 20, start.y + 50);
    }

    public void onLoad() {
    }

    public void onClose() {
    }
}

