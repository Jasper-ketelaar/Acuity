package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.rs.utils.RsMapService;

import java.awt.*;

public class PositionPlugin extends Plugin {

    @Override
    public void onPaint(Graphics2D graphics) {
        getPaintUtil().debug("Cursor (Map): " + getPerspective().screenToLocation(this.getMapPanel().getMousePosition()));
        getPaintUtil().debug("Cursor: " + this.getMapPanel().getMousePosition());
        getPaintUtil().debug("View Base: " + getPerspective().getBase());
        getPaintUtil().debug("Region: " + RsMapService.worldToRegionId(getPerspective().screenToLocation(this.getMapPanel().getMousePosition())));
    }

    public void onLoad() {
    }

    public void onClose() {
    }
}

