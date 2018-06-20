package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * Created by Zachary Herridge on 6/18/2018.
 */
public class HpaPlugin extends Plugin{

    private Map<String, HPARegion> graph;
    private HPARegion curHPARegion;

    public void setGraph(Map<String, HPARegion> graph) {
        this.graph = graph;
    }

    @Override
    public void onPaint(Graphics2D graphics, Graphics2D scaledGraphics) {
        if (graph == null) return;

        if (curHPARegion != null){
            getPaintUtil().fillArea(
                    graphics,
                    curHPARegion.getRoot().clone(0, curHPARegion.getHeight() - 1),
                    curHPARegion.getWidth(),
                    curHPARegion.getHeight(),
                    Color.RED
            );
        }

        for (HPARegion HPARegion : graph.values()) {
            for (HPANode hpaNode : HPARegion.getNodes().values()) {
                getPaintUtil().markLocation(graphics, hpaNode.getLocation(), hpaNode.getType() == HPANode.STAIR ? Color.RED : Color.BLUE);

                for (Edge edge : hpaNode.getNeighbors()) {
                    if (edge instanceof HPAEdge){
                        java.util.List<Edge> path = ((HPAEdge) edge).getPath();
                        for (Edge edge1 : path) {
                            getPaintUtil().connectLocations(graphics, edge1.getStart(), edge1.getEnd(), Color.BLUE);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isAltDown()){
            Location mouseLocation = getMapPanel().getMouseLocation();
            if (graph != null && mouseLocation != null){
                for (HPARegion HPARegion : graph.values()) {
                    if (HPARegion.contains(mouseLocation)){
                        curHPARegion = HPARegion;
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
