package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;

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
            for (HPANode hpaNode : HPARegion.getNodes()) {
                for (Edge edge : hpaNode.getNeighbors()) {
                    Color color = ((HPAEdge) edge).isInternal() ? Color.BLUE : Color.ORANGE;
                    getPaintUtil().connectLocations(graphics, edge.getStart(), edge.getEnd(), color);
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
