package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.path_finding.algorithms.astar.AStarService;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapPanel;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Created by Zachary Herridge on 6/18/2018.
 */
@org.springframework.stereotype.Component
public class PathPlugin extends Plugin {

    private final AStarService aStarService;

    private Location l1, l2;
    private List<Edge> path;

    public PathPlugin(AStarService aStarService) {
        this.aStarService = aStarService;
    }

    @Override
    public void onPaint(Graphics2D graphics, Graphics2D scaledGraphics) {
        if (l1 != null) getPaintUtil().markTile(graphics, l1, Color.RED);
        if (l2 != null) getPaintUtil().markTile(graphics, l1, Color.GREEN);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isControlDown()){
            if (e.isShiftDown()){
                l2 = getMapPanel().getMouseLocation();
            }
            else {
                l1 = getMapPanel().getMouseLocation();
            }

            if (l1 != null && l2 != null){
                path = findPath(l1, l2).orElse(null);
            }
        }
    }

    private Optional<java.util.List<Edge>> findPath(Locateable start, Locateable end){
        return aStarService.findPath(
                new LocateableHeuristic(),
                RsEnvironment.getNode(start.getLocation()),
                RsEnvironment.getNode(end.getLocation())
        );
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onClose() {

    }
}
