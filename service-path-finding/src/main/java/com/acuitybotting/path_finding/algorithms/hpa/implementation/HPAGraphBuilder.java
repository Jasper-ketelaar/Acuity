package com.acuitybotting.path_finding.algorithms.hpa.implementation;


import com.acuitybotting.db.arango.path_finding.domain.SceneEntity;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.domain.location.LocationPair;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class HPAGraphBuilder {

    private Location lower, upper;
    private int regionWidth, regionHeight;

    private Map<String, HPARegion> regions;

    private int internalNodeConnectionLimit = 4;

    private int externalConnectionsCount = 0;
    private int internalConnectionCount = 0;
    private int stairNodesAddedCount = 0;
    private int stairNodeConnectionsAddedCount = 0;

    private PathFindingSupplier pathFindingSupplier;

    public Map<String, HPARegion> init(Location lower, Location upper) {
        this.lower = lower;
        this.upper = upper;

        regions = findRegions();
        log.info("Initiated with {} regions.", regions.size());

        return regions;
    }

    public Map<String, HPARegion> build(PathFindingSupplier pathFindingSupplier) {
        this.pathFindingSupplier = pathFindingSupplier;
        log.info("Started building HPA graph between {} and {} with width {} and height {}.", lower, upper, regionWidth, regionHeight);

        long startTimestamp = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(20);

        for (HPARegion internalHPARegion : regions.values()) {
            executorService.submit(() -> {
                List<LocationPair> externalConnections = findExternalConnections(internalHPARegion, pathFindingSupplier);
                log.info("Found {} external connections from {}.", externalConnections.size(), internalHPARegion);
                for (LocationPair externalConnection : externalConnections) {
                    HPARegion externalHPARegion = getRegionContaining(externalConnection.getEnd());

                    HPANode internalNode = internalHPARegion.getOrCreateNode(externalConnection.getStart());
                    HPANode externalNode = externalHPARegion.getOrCreateNode(externalConnection.getEnd());

                    internalNode.addConnection(externalNode);
                    externalNode.addConnection(internalNode);

                    externalConnectionsCount++;
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("Found {} external connections.", externalConnectionsCount);


        executorService = Executors.newFixedThreadPool(20);

        for (HPARegion hpaRegion : regions.values()) {
            executorService.submit(() -> addStairConnections(hpaRegion));
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        log.info("Found {} stair nodes with {} connections.", stairNodesAddedCount, stairNodeConnectionsAddedCount);

        executorService = Executors.newFixedThreadPool(10);
        for (HPARegion region : regions.values()) {
            executorService.submit(() -> {
                for (HPANode startNode : region.getNodes().values()) {
                    findInternalConnections(region, startNode);
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("Found {} internal connections.", internalConnectionCount);

        log.info("Finished creating HPA graph in {} seconds.", (System.currentTimeMillis() - startTimestamp) / 1000);

        return regions;
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
                startNode.addConnection(endNode, path);
                endNode.addConnection(startNode, path);
            }
        }
    }

    private void addStairConnections(HPARegion region) {
        for (SceneEntity sceneEntity : RsEnvironment.getStairsWithin(region)) {
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
                        stairNodeBase.addConnection(stairNodeUpper);
                        stairNodesAddedCount++;
                        stairNodeConnectionsAddedCount++;
                    }
                }

                if (down){
                    HPANode stairNodeUpper = createStairNode(stairLocation.clone(0, 0, -1));
                    if (stairNodeUpper != null){
                        stairNodeBase.addConnection(stairNodeUpper);
                        stairNodesAddedCount++;
                        stairNodeConnectionsAddedCount++;
                    }
                }
            }
        }
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
                    HPARegion region = new HPARegion(new Location(x, y, z), regionWidth, regionHeight);
                    regions.put(region.getKey(), region);
                }
            }
        }
        return regions;
    }

    public HPAGraphBuilder setRegionHeight(int regionHeight) {
        this.regionHeight = regionHeight;
        return this;
    }

    public HPAGraphBuilder setRegionWidth(int regionWidth) {
        this.regionWidth = regionWidth;
        return this;
    }

    public Map<String, HPARegion> getRegions() {
        return regions;
    }
}
