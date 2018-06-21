package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.TemporaryNode;
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

    private HPAGraph graph;

    private Location start, end;

    private HPARegion startRegion, endRegion;
    private HPANode startNode, endNode;

    private java.util.List<Edge> path;
    private Executor executor = Executors.newSingleThreadExecutor();

    private AStarImplementation aStarImplementation;

    private Color[] nodeColorings = new Color[]{Color.BLUE, Color.RED, Color.CYAN};

    public void setGraph(HPAGraph graph) {
        this.graph = graph;
    }

    @Override
    public void onPaint(Graphics2D graphics, Graphics2D scaledGraphics) {
        if (graph == null) return;

        if (startNode != null){
            for (Edge edge : startNode.getEdges()) {
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

                for (Edge edge : hpaNode.getEdges()) {
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
                    if (endNode != null) endNode.unlink();
                    endNode = new TemporaryNode(endRegion, end);
                    graph.findInternalConnections(endRegion, endNode, 8);
                }
                getMapPanel().repaint();
            } else {
                start = getMapPanel().getMouseLocation();
                startRegion = graph.getRegionContaining(start);
                if (startRegion != null) {
                    if (startNode != null) startNode.unlink();
                    startNode = new TemporaryNode(startRegion, start).addStartEdges();
                    graph.findInternalConnections(startRegion, startNode, 8);
                }

                getMapPanel().repaint();
            }

            if (startNode != null && endNode != null) {
                executor.execute(() -> {
                    aStarImplementation = new AStarImplementation()
                            .setEdgePredicate(edge -> {
                                Node end = edge.getEnd();
                                return !(edge instanceof TemporaryNode) || end.equals(endNode);
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
