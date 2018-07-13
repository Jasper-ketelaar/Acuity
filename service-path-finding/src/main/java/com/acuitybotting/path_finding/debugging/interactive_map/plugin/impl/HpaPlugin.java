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
import com.acuitybotting.path_finding.rs.domain.location.LocationPair;
import com.acuitybotting.path_finding.rs.utils.EdgeType;

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
                    if (((HPAEdge) edge).getType() == EdgeType.CUSTOM){
                        getPaintUtil().connectLocations(graphics, edge.getStart(), edge.getEnd(), Color.BLACK);
                    }
                }
            }
        }


        for (HPARegion HPARegion : graph.getRegions().values()) {
            for (HPANode hpaNode : HPARegion.getNodes().values()) {
                getPaintUtil().markLocation(graphics, hpaNode.getLocation(), nodeColorings[hpaNode.getType()]);
            }

            for (HPANode hpaNode : HPARegion.getNodes().values()) {
                for (Edge edge : hpaNode.getNeighbors()) {
                    if (edge instanceof HPAEdge) {
                        getPaintUtil().connectLocations(graphics, edge.getStart(), edge.getEnd(), Color.BLUE);
                    }
                }
            }
        }

        if (aStarImplementation != null && aStarImplementation.isDebugMode()) {
            Set<Node> costCache = aStarImplementation.getEvaluated();
            if (costCache != null) {
                for (Node node : costCache) {
                    getPaintUtil().markLocation(graphics, node, Color.ORANGE);
                }
            }
        }


        Location click = getMapPanel().getMouseLocation();
        HPARegion clickRegion = graph.getRegionContaining(click);
        if (clickRegion != null){
            Location l1 = clickRegion.getRoot();
            Location l2 = clickRegion.getRoot().clone(clickRegion.getWidth() - 1, 0);
            Location l3 = clickRegion.getRoot().clone(clickRegion.getWidth() - 1 , clickRegion.getHeight() - 1);
            Location l4 = clickRegion.getRoot().clone(0, clickRegion.getHeight() -  1);

            getPaintUtil().connectLocations(graphics, l1, l2, Color.MAGENTA);
            getPaintUtil().connectLocations(graphics, l2, l3, Color.MAGENTA);
            getPaintUtil().connectLocations(graphics, l3, l4, Color.MAGENTA);
            getPaintUtil().connectLocations(graphics, l4, l1, Color.MAGENTA);


            List<LocationPair> externalConnections = graph.findExternalConnections(clickRegion, graph.getPathFindingSupplier());
            for (LocationPair externalConnection : externalConnections) {
                getPaintUtil().connectLocations(graphics, externalConnection.getStart(), externalConnection.getEnd(), Color.BLUE);
            }

        }

        HPANode hpaNode = clickRegion.getNodes().get(click);
        if (hpaNode != null){
            for (Edge edge : hpaNode.getNeighbors()) {
                getPaintUtil().connectLocations(graphics, edge.getStart(), edge.getEnd(), Color.MAGENTA);
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


            if (e.isAltDown()){
                Location click = getMapPanel().getMouseLocation();
                HPARegion clickRegion = graph.getRegionContaining(click);
                HPANode hpaNode = clickRegion.getNodes().get(click);
                if (hpaNode != null) {
                    Set<HPAGraph.InternalConnection> internalConnections = graph.findInternalConnections(clickRegion, hpaNode, 90);
                    System.out.println(hpaNode.getNeighbors());
                }
                return;
            }


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
