package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.Region;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Zachary Herridge on 6/18/2018.
 */
public class HpaPlugin extends Plugin{

    private Map<Location, HPANode> graph;
    private Region curRegion;

    public void setGraph(Map<Location, HPANode> graph) {
        this.graph = graph;
    }

    @Override
    public void onPaint(Graphics2D graphics, Graphics2D scaledGraphics) {
        if (graph == null) return;

        if (curRegion != null){
            getPaintUtil().fillArea(
                    graphics,
                    curRegion.getRoot().clone(0, curRegion.getHeight() - 1),
                    curRegion.getWidth(),
                    curRegion.getHeight(),
                    Color.RED
            );
        }

        for (HPANode hpaNode : graph.values()) {
            for (Edge edge : hpaNode.getNeighbors()) {
                Color color = ((HPAEdge) edge).isInternal() ? Color.BLUE : Color.ORANGE;
                getPaintUtil().connectLocations(graphics, edge.getStart(), edge.getEnd(), color);
            }
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isAltDown()){
            Location mouseLocation = getMapPanel().getMouseLocation();
            if (graph != null && mouseLocation != null){
                for (HPANode hpaNode : graph.values()) {
                    Region region = hpaNode.getRegion();
                    if (region != null && region.contains(mouseLocation)){
                        curRegion = region;
                        return;
                    }
                }
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
