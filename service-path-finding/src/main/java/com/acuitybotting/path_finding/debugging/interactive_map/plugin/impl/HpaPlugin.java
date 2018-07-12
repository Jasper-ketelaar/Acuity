package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.TerminatingNode;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.rs.domain.location.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by Zachary Herridge on 6/18/2018.
 */
public class HpaPlugin extends Plugin {

    private HPAGraph graph;

    private Location start, end;

    private HPARegion startRegion, endRegion;
    private TerminatingNode startNode, endNode;

    private java.util.List<Edge> path;
    private Executor executor = ExecutorUtil.newExecutorPool(1);

    private AStarImplementation aStarImplementation;

    private Color[] nodeColorings = new Color[]{Color.BLUE, Color.RED, Color.CYAN};

    public void setGraph(HPAGraph graph) {
        this.graph = graph;
    }

    @Override
    public void onPaint(Graphics2D graphics) {
        if (graph == null) return;

        if (startNode != null){
            for (Edge edge : startNode.getNeighbors()) {
                if (edge instanceof HPAEdge){
                    if (((HPAEdge) edge).getType() == HPANode.CUSTOM){
                        getPaintUtil().connectLocations(graphics, edge.getStart(), edge.getEnd(), Color.BLACK);
                    }
                }
            }
        }

        for (HPARegion HPARegion : graph.getRegions().values()) {
            for (HPANode hpaNode : HPARegion.getNodes().values()) {
                getPaintUtil().markLocation(graphics, hpaNode.getLocation(), nodeColorings[hpaNode.getType()]);
                for (Edge edge : hpaNode.getNeighbors()) {
                    if (edge instanceof HPAEdge) {
                        getPaintUtil().connectLocations(graphics, edge.getStart(), edge.getEnd(), Color.BLUE);
                    }
                }
            }
        }

        if (aStarImplementation != null && aStarImplementation.isDebugMode()) {
            Map<Node, Double> costCache = aStarImplementation.getCostCache();
            if (costCache != null) {
                for (Node node : costCache.keySet()) {
                    getPaintUtil().markLocation(graphics, node, Color.ORANGE);
                }
            }
        }

        if (path != null) {
            for (Edge edge : path) {
                getPaintUtil().connectLocations(graphics, edge.getStart(), edge.getEnd(), Color.MAGENTA);
            }
        }

        if (startNode != null) getPaintUtil().markLocation(graphics, startNode, Color.RED);
        if (endNode != null) getPaintUtil().markLocation(graphics, endNode, Color.GREEN);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isControlDown()) {
            if (e.isShiftDown()) {
                end = getMapPanel().getMouseLocation();
                endRegion = graph.getRegionContaining(end);
                if (endRegion != null) {
                    if (endNode != null) endNode.disconnectFromGraph();
                    endNode = new TerminatingNode(endRegion, end);
                    endNode.connectToGraph();
                }
                getMapPanel().repaint();
            } else {
                start = getMapPanel().getMouseLocation();
                startRegion = graph.getRegionContaining(start);
                if (startRegion != null) {
                    if (startNode != null) startNode.disconnectFromGraph();
                    startNode = new TerminatingNode(startRegion, start);
                    startNode.connectToGraph();
                }

                getMapPanel().repaint();
            }

            if (startNode != null && endNode != null) {
                executor.execute(() -> {
                    aStarImplementation = new AStarImplementation()
                            .setEdgePredicate(edge -> {
                                Node end = edge.getEnd();
                                return !(edge instanceof TerminatingNode) || end.equals(endNode);
                            })
                            .setDebugMode(true);
                    path = aStarImplementation.findPath(new LocateableHeuristic(), startNode, endNode).orElse(null);
                    getMapPanel().repaint();
                });
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
