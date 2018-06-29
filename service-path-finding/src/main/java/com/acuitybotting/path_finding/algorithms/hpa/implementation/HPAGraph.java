package com.acuitybotting.path_finding.algorithms.hpa.implementation;


import com.acuitybotting.db.arango.path_finding.domain.SceneEntity;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.rs.custom_edges.CustomEdge;
import com.acuitybotting.path_finding.rs.custom_edges.edges.LocationTiedEdges;
import com.acuitybotting.path_finding.rs.custom_edges.edges.PlayerTiedEdges;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.domain.location.LocationPair;
import com.acuitybotting.path_finding.rs.utils.ExecutorUtil;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class HPAGraph {

    private Location lower;
    private Location upper;
    private int regionWidth;
    private int regionHeight;

    private Map<String, HPARegion> regions = new HashMap<>();

    private int internalNodeConnectionLimit = 4;

    private int customNodesAddedCount = 0;
    private int externalConnectionsCount = 0;
    private int internalConnectionCount = 0;
    private int stairNodesAddedCount = 0;
    private int stairNodeConnectionsAddedCount = 0;

    private PathFindingSupplier pathFindingSupplier;

    public Map<String, HPARegion> init(Location lower, Location upper, int regionWidth, int regionHeight) {
        this.lower = lower;
        this.upper = upper;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
        return regions;
    }

    public HPAGraph setPathFindingSupplier(PathFindingSupplier pathFindingSupplier) {
        this.pathFindingSupplier = pathFindingSupplier;
        return this;
    }

    public Map<String, HPARegion> build() {
        log.info("Started building HPA graph between {} and {} with width {} and height {}.", lower, upper, regionWidth, regionHeight);
        long startTimestamp = System.currentTimeMillis();

        regions = findRegions();
        log.info("Initiated with {} regions.", regions.size());

        ExecutorUtil.run(20, executor -> {
            for (HPARegion internalHPARegion : regions.values()) {
                executor.execute(() -> {
                    List<LocationPair> externalConnections = findExternalConnections(internalHPARegion, pathFindingSupplier);
                    log.debug("Found {} external connections from {}.", externalConnections.size(), internalHPARegion);
                    for (LocationPair externalConnection : externalConnections) {
                        HPARegion externalHPARegion = getRegionContaining(externalConnection.getEnd());

                        HPANode internalNode = internalHPARegion.getOrCreateNode(externalConnection.getStart());
                        HPANode externalNode = externalHPARegion.getOrCreateNode(externalConnection.getEnd());

                        internalNode.addConnection(externalNode, HPANode.GROUND);
                        externalNode.addConnection(internalNode, HPANode.GROUND);

                        externalConnectionsCount++;
                    }
                });
            }
        });

        log.info("Found {} external connections.", externalConnectionsCount);


        ExecutorUtil.run(20, executor -> {
            for (HPARegion hpaRegion : regions.values()) {
                executor.execute(() -> addStairConnections(hpaRegion));
            }
        });

        log.info("Found {} stair nodes with {} connections.", stairNodesAddedCount, stairNodeConnectionsAddedCount);

        ExecutorUtil.run(20, executor -> {
            for (HPARegion region : regions.values()) {
                executor.execute(() -> {
                    for (HPANode startNode : region.getNodes().values()) {
                        findInternalConnections(region, startNode);
                    }
                });
            }
        });

        log.info("Found {} internal connections.", internalConnectionCount);

        log.info("Finished creating HPA graph in {} seconds.", (System.currentTimeMillis() - startTimestamp) / 1000);

        return regions;
    }

    public HPAGraph addCustomNodes(){
        for (CustomEdge customEdge : PlayerTiedEdges.getEdges()) {
            HPARegion endRegion = getRegionContaining(customEdge.getEnd());
            if (endRegion == null) continue;
            findInternalConnections(endRegion, endRegion.getOrCreateNode(customEdge.getEnd(), HPANode.CUSTOM));
            customNodesAddedCount++;
        }

        for (CustomEdge customEdge : LocationTiedEdges.getEdges()) {
            HPARegion startRegion = getRegionContaining(customEdge.getStart());
            HPARegion endRegion = getRegionContaining(customEdge.getEnd());
            if (startRegion == null || endRegion == null) continue;
            HPANode startNode = startRegion.getOrCreateNode(customEdge.getStart(), HPANode.CUSTOM);
            HPANode endNode = endRegion.getOrCreateNode(customEdge.getEnd(), HPANode.CUSTOM);

            findInternalConnections(startRegion, startNode);
            findInternalConnections(endRegion, endNode);

            startNode.addConnection(endNode, HPANode.CUSTOM);

            customNodesAddedCount += 2;
        }

        log.info("Added {} custom nodes.", customNodesAddedCount);

        return this;
    }

    public void findInternalConnections(HPARegion region, HPANode startNode) {
        findInternalConnections(region, startNode, internalNodeConnectionLimit);
    }

    public void findInternalConnections(HPARegion region, HPANode startNode, int limit) {
        List<HPANode> endNodes = region.getNodes().values().stream()
                .filter(hpaNode -> !hpaNode.equals(startNode))
                .sorted(Comparator.comparingDouble(o -> o.getLocation().getTraversalCost(startNode.getLocation())))
                .collect(Collectors.toList());

        int found = 0;
        for (HPANode endNode : endNodes) {
            if (limit != 0 && found >= limit) break;

            if (startNode.getEdges().stream().anyMatch(edge -> edge.getEnd().equals(endNode))) {
                found++;
                continue;
            }

            List<Edge> path = pathFindingSupplier.findPath(
                    startNode.getLocation(),
                    endNode.getLocation(),
                    edge -> limitToRegion(region, edge)
            ).orElse(null);

            if (path != null) {
                found++;
                internalConnectionCount++;
                startNode.addConnection(endNode, HPANode.GROUND, path);
                endNode.addConnection(startNode, HPANode.GROUND, path);
            }
        }
    }

    private void addStairConnections(HPARegion region) {
    /*    for (SceneEntity sceneEntity : RsEnvironment.getStairsWithin(region)) {
            if (sceneEntity.getActions() == null) continue;
            boolean up = Arrays.stream(sceneEntity.getActions()).anyMatch(s -> s.toLowerCase().contains("up"));
            boolean down = Arrays.stream(sceneEntity.getActions()).anyMatch(s -> s.toLowerCase().contains("down"));

            if (up || down) {
                Location stairLocation = new Location(sceneEntity.getX(), sceneEntity.getY(), sceneEntity.getPlane());

                HPANode stairNodeBase = createStairNode(stairLocation);
                if (stairNodeBase == null) continue;
                stairNodesAddedCount++;

                if (up){
                    HPANode stairNodeUpper = createStairNode(stairLocation.clone(0, 0, 1));
                    if (stairNodeUpper != null){
                        stairNodeBase.addConnection(stairNodeUpper, HPANode.STAIR);
                        stairNodesAddedCount++;
                        stairNodeConnectionsAddedCount++;
                    }
                }

                if (down){
                    HPANode stairNodeUpper = createStairNode(stairLocation.clone(0, 0, -1));
                    if (stairNodeUpper != null){
                        stairNodeBase.addConnection(stairNodeUpper, HPANode.STAIR);
                        stairNodesAddedCount++;
                        stairNodeConnectionsAddedCount++;
                    }
                }

                System.out.println("Adding stairs. make this better.");
            }
        }*/
    }

    private HPANode createStairNode(Location stairLocation){
        TileNode connectionPoint = (TileNode) new TileNode(stairLocation).getNeighbors(true).stream().map(Edge::getEnd).findAny().orElse(null);
        if (connectionPoint == null) return null;

        HPARegion regionContaining = getRegionContaining(connectionPoint);
        if (regionContaining == null) return null;

        return regionContaining.getOrCreateNode(connectionPoint.getLocation(), HPANode.STAIR);
    }

    public HPARegion getRegionContaining(Locateable locateable) {
        Location offset = locateable.getLocation().subtract(lower);
        int offX = ((int) (offset.getX() / (double) regionWidth)) * regionWidth;
        int offY = ((int) (offset.getY() / (double) regionHeight)) * regionHeight;
        Location base = lower.clone(offX, offY);
        return regions.get(HPARegion.getKey(base.getX(), base.getY(), base.getPlane(), regionWidth, regionHeight));

    }

    private boolean limitToRegion(HPARegion region, Edge edge) {
        if (region == null) return false;
        Node node = edge.getStart();
        if (node == null || node instanceof Locateable && !region.contains(((Locateable) node).getLocation()))
            return false;
        node = edge.getEnd();
        if (node == null || node instanceof Locateable && !region.contains(((Locateable) node).getLocation()))
            return false;
        return true;
    }

    private List<LocationPair> findExternalConnections(HPARegion region, PathFindingSupplier pathFindingSupplier) {
        List<LocationPair> connections = new ArrayList<>();
        connections.addAll(filterEdgeConnections(region.getEdgeConnections(0), pathFindingSupplier));
        connections.addAll(filterEdgeConnections(region.getEdgeConnections(1), pathFindingSupplier));
        connections.addAll(filterEdgeConnections(region.getEdgeConnections(2), pathFindingSupplier));
        connections.addAll(filterEdgeConnections(region.getEdgeConnections(3), pathFindingSupplier));
        return connections;
    }

    private List<LocationPair> filterEdgeConnections(List<LocationPair> connections, PathFindingSupplier pathFindingSupplier) {
        boolean lastPairConnected = false;
        for (LocationPair connection : new ArrayList<>(connections)) {
            if (getRegionContaining(connection.getEnd()) == null) {
                connections.remove(connection);
                continue;
            }

            boolean directlyConnected = pathFindingSupplier.isDirectlyConnected(connection.getStart(), connection.getEnd());
            if (lastPairConnected || !directlyConnected) connections.remove(connection);
            lastPairConnected = directlyConnected;
        }
        return connections;
    }

    private Map<String, HPARegion> findRegions() {
        Map<String, HPARegion> regions = new HashMap<>();
        for (int z = lower.getPlane(); z <= upper.getPlane(); z++) {
            for (int x = lower.getX(); x <= upper.getX(); x += regionWidth) {
                for (int y = lower.getY(); y <= upper.getY(); y += regionHeight) {
                    HPARegion region = new HPARegion(this, new Location(x, y, z), regionWidth, regionHeight);
                    regions.put(region.getKey(), region);
                }
            }
        }
        return regions;
    }

    public Map<String, HPARegion> getRegions() {
        return regions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HPAGraph{");
        sb.append("lower=").append(lower);
        sb.append(", upper=").append(upper);
        sb.append(", regionWidth=").append(regionWidth);
        sb.append(", regionHeight=").append(regionHeight);
        sb.append('}');
        return sb.toString();
    }
}
