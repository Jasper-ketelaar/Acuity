package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraphBuilder;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.TerminatingNode;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Zachary Herridge on 6/18/2018.
 */
public class HpaPlugin extends Plugin {

    private HPAGraphBuilder graph;

    private Location start, end;

    private HPARegion startRegion, endRegion;
    private HPANode startNode, endNode;

    private java.util.List<Edge> path;
    private Executor executor = Executors.newSingleThreadExecutor();

    private AStarImplementation aStarImplementation;

    public void setGraph(HPAGraphBuilder graph) {
        this.graph = graph;
    }

    @Override
    public void onPaint(Graphics2D graphics, Graphics2D scaledGraphics) {
        if (graph == null) return;


        for (HPARegion HPARegion : graph.getRegions().values()) {
            for (HPANode hpaNode : HPARegion.getNodes().values()) {
                getPaintUtil().markLocation(graphics, hpaNode.getLocation(), hpaNode.getType() == HPANode.STAIR ? Color.RED : Color.BLUE);

                for (Edge edge : hpaNode.getNeighbors()) {
                    if (edge instanceof HPAEdge) {
                        getPaintUtil().connectLocations(graphics, ((HPAEdge) edge).getPath(), Color.BLUE);
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
                getPaintUtil().connectLocations(graphics, ((HPAEdge) edge).getPath(), Color.MAGENTA);
            }
        }

        if (start != null) {
            getPaintUtil().markLocation(graphics, start, Color.RED);
            getPaintUtil().connectLocations(graphics, new TileNode(start).getNeighbors(true), Color.RED);
        }
        if (end != null) getPaintUtil().markLocation(graphics, end, Color.GREEN);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isControlDown()) {
            if (e.isShiftDown()) {
                end = getMapPanel().getMouseLocation();
                endRegion = graph.getRegionContaining(end);
                if (endRegion != null) {
                    if (endNode != null) endNode.unlink();
                    endNode = new TerminatingNode(endRegion, end);
                    graph.findInternalConnections(endRegion, endNode, 8);
                }
                getMapPanel().repaint();
            } else {
                start = getMapPanel().getMouseLocation();
                startRegion = graph.getRegionContaining(start);
                if (startRegion != null) {
                    if (startNode != null) startNode.unlink();
                    startNode = new TerminatingNode(startRegion, start);
                    graph.findInternalConnections(startRegion, startNode, 8);
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
