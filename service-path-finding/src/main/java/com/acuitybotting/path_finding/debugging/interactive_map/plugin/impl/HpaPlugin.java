package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.awt.*;
import java.util.Map;

/**
 * Created by Zachary Herridge on 6/18/2018.
 */
public class HpaPlugin extends Plugin{

    private Map<Location, HPANode> graph;

    public void setGraph(Map<Location, HPANode> graph) {
        this.graph = graph;
    }

    @Override
    public void onPaint(Graphics2D graphics, Graphics2D scaledGraphics) {
        if (graph == null) return;

        for (HPANode hpaNode : graph.values()) {
            for (Edge edge : hpaNode.getNeighbors()) {
                getPaintUtil().connectLocations(graphics, edge.getStart(), edge.getEnd(), Color.BLUE);
            }
        }

    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onClose() {

    }
}
