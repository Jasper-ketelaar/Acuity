package com.acuitybotting.path_finding.algorithms.hpa.implementation;


import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.rs.custom_edges.CustomEdge;
import com.acuitybotting.path_finding.rs.custom_edges.edges.LocationTiedEdges;
import com.acuitybotting.path_finding.rs.custom_edges.edges.PlayerTiedEdges;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.domain.location.LocationPair;
import com.acuitybotting.path_finding.rs.utils.MapFlags;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.xtea.domain.rs.cache.RsRegion;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Predicate;
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
            for (HPARegion hpaRegion : regions.values()) {
                executor.execute(() -> addStairConnections(hpaRegion));
            }
        });
        log.info("Found {} stair connections.", stairNodeConnectionsAddedCount);

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

            boolean ignoreStartBlocked = startNode.getType() == HPANode.STAIR;

            List<Edge> path = findInternalPath(
                    startNode.getLocation(),
                    endNode.getLocation(),
                    region,
                    ignoreStartBlocked
            );

            if (path != null) {
                found++;
                internalConnectionCount++;
                String pathKey = RsEnvironment.getRsMap().addPath(path);
                startNode.addConnection(endNode, HPANode.GROUND, path.size()).setPathKey(pathKey);
                endNode.addConnection(startNode, HPANode.GROUND, path.size()).setPathKey(pathKey);
            }
        }
    }

    public List<Edge> findInternalPath(Location start, Location end, HPARegion limit, boolean ignoreStartBlocked){
        return pathFindingSupplier.findPath(
                start,
                end,
                edge -> limitToRegion(limit, edge),
                ignoreStartBlocked
        ).orElse(null);
    }

    private void addStairConnections(HPARegion region) {
        for (int plane = 0; plane < RsRegion.Z; plane++) {
            for (int x = 0; x < RsRegion.X; x++) {
                for (int y = 0; y < RsRegion.Y; y++) {
                    Location location = region.getRoot().clone(x, y, plane);

                    Integer flag = RsEnvironment.getRsMap().getFlagAt(location).orElse(null);
                    if (flag == null) continue;
                    if (MapFlags.check(flag, MapFlags.PLANE_CHANGE_UP)){
                        if (plane + 1 >= RsRegion.Z) continue;

                        HPANode start = region.getOrCreateNode(location, HPANode.STAIR);
                        HPANode end = region.getOrCreateNode(location.clone(0, 0, 1), HPANode.STAIR);
                        start.addConnection(end, HPANode.STAIR);
                        stairNodeConnectionsAddedCount++;
                    }

                    if (MapFlags.check(flag, MapFlags.PLANE_CHANGE_DOWN)){
                        if (plane - 1 < 0) continue;
                        HPANode start = region.getOrCreateNode(location, HPANode.STAIR);
                        HPANode end = region.getOrCreateNode(location.clone(0, 0, -1), HPANode.STAIR);
                        start.addConnection(end, HPANode.STAIR);
                        stairNodeConnectionsAddedCount++;
                    }
                }
            }
        }
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
        List<LocationPair> goodConnections = new ArrayList<>();
        boolean lastPairConnected = false;
        for (LocationPair connection : connections) {
            if (getRegionContaining(connection.getEnd()) == null) {
                continue;
            }

            boolean directlyConnected = pathFindingSupplier.isDirectlyConnected(connection.getStart(), connection.getEnd());

            if (!lastPairConnected && directlyConnected) goodConnections.add(connection);
            lastPairConnected = directlyConnected;
        }
        return goodConnections;
    }

    private Map<String, HPARegion> findRegions() {
        Map<String, HPARegion> regions = new HashMap<>();
        for (int z = lower.getPlane(); z <= upper.getPlane(); z++) {
            for (int x = lower.getX(); x < upper.getX(); x += regionWidth) {
                for (int y = lower.getY(); y < upper.getY(); y += regionHeight) {
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
