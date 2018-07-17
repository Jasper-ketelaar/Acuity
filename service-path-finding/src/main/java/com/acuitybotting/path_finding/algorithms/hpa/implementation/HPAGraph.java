package com.acuitybotting.path_finding.algorithms.hpa.implementation;


import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.rs.custom_edges.CustomEdgeData;
import com.acuitybotting.path_finding.rs.custom_edges.edges.LocationTiedEdges;
import com.acuitybotting.path_finding.rs.custom_edges.edges.PlayerTiedEdges;
import com.acuitybotting.path_finding.rs.domain.graph.TileEdge;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.domain.location.LocationPair;
import com.acuitybotting.path_finding.rs.utils.EdgeType;
import com.acuitybotting.path_finding.rs.utils.MapFlags;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.xtea.domain.rs.cache.RsRegion;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
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

                        internalNode.addHpaEdge(externalNode, EdgeType.BASIC);
                        externalNode.addHpaEdge(internalNode, EdgeType.BASIC);

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
                        addInternalConnections(region, startNode);
                    }
                });
            }
        });
        log.info("Found {} internal connections.", internalConnectionCount);

        log.info("Finished creating HPA graph in {} seconds.", (System.currentTimeMillis() - startTimestamp) / 1000);

        return regions;
    }

    public HPAGraph addCustomNodes(){
        //TODO
        return this;
    }

    private void addInternalConnections(HPARegion region, HPANode startNode) {
        for (InternalConnection internalConnection : findInternalConnections(region, startNode, internalNodeConnectionLimit)) {
            internalConnection.getStart()
                    .addHpaEdge(internalConnection.getEnd(), EdgeType.BASIC, internalConnection.getPath().size())
                    .setPathKey(RsEnvironment.getRsMap().addPath(internalConnection.getPath(), false));

            internalConnection.getEnd()
                    .addHpaEdge(internalConnection.getStart(), EdgeType.BASIC, internalConnection.getPath().size())
                    .setPathKey(RsEnvironment.getRsMap().addPath(internalConnection.getPath(), true));
        }
    }


    @Getter
    @Setter
    public static class InternalConnection {

        private HPANode start, end;
        private List<TileEdge> path;

    }

    public Set<InternalConnection> findInternalConnections(HPARegion region, HPANode startNode, int limit) {
        Set<InternalConnection> internalConnections = new HashSet<>();

        List<HPANode> endNodes = region.getNodes().values().stream()
                .filter(hpaNode -> !startNode.equals(hpaNode)) //Order of this statement matters do not change it.
                .sorted(Comparator.comparingDouble(o -> o.getLocation().getTraversalCost(startNode.getLocation())))
                .collect(Collectors.toList());

        int found = 0;
        for (HPANode endNode : endNodes) {
            if (limit > 0 && found >= limit) {
                evaluateInternalConnection(internalConnections, region, startNode, endNodes.get(endNodes.size() - 1));
                break;
            }

            if (evaluateInternalConnection(internalConnections, region, startNode, endNode)) found++;
        }

        return internalConnections;
    }

    private boolean evaluateInternalConnection(Set<InternalConnection> internalConnections, HPARegion region, HPANode startNode, HPANode endNode){
        if (startNode.getHpaEdges().stream().anyMatch(edge -> edge.getEnd().equals(endNode))) {
            return true;
        }

        Boolean ignoreStartBlocked = RsEnvironment.getRsMap().getFlagAt(startNode.getLocation()).map(flag -> MapFlags.check(flag, MapFlags.PLANE_CHANGE_DOWN | MapFlags.PLANE_CHANGE_UP)).orElse(false);

        List<TileEdge> path = findInternalPath(
                startNode.getLocation(),
                endNode.getLocation(),
                region,
                ignoreStartBlocked
        );

        if (path != null) {
            internalConnectionCount++;

            InternalConnection internalConnection = new InternalConnection();
            internalConnection.setPath(path);
            internalConnection.setStart(startNode);
            internalConnection.setEnd(endNode);
            internalConnections.add(internalConnection);
            return true;
        }

        return false;
    }


    @SuppressWarnings("unchecked")
    public List<TileEdge> findInternalPath(Location start, Location end, HPARegion limit, boolean ignoreStartBlocked){
        return (List<TileEdge>) pathFindingSupplier.findPath(
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

                        HPANode start = region.getOrCreateNode(location, EdgeType.PLANE_CHANGE);
                        HPANode end = region.getOrCreateNode(location.clone(0, 0, 1), EdgeType.PLANE_CHANGE);
                        start.addHpaEdge(end, EdgeType.PLANE_CHANGE).setType(EdgeType.PLANE_CHANGE);
                        stairNodeConnectionsAddedCount++;
                    }

                    if (MapFlags.check(flag, MapFlags.PLANE_CHANGE_DOWN)){
                        if (plane - 1 < 0) continue;
                        HPANode start = region.getOrCreateNode(location, EdgeType.PLANE_CHANGE);
                        HPANode end = region.getOrCreateNode(location.clone(0, 0, -1), EdgeType.PLANE_CHANGE);
                        start.addHpaEdge(end, EdgeType.PLANE_CHANGE).setType(EdgeType.PLANE_CHANGE);
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

    public List<LocationPair> findExternalConnections(HPARegion region, PathFindingSupplier pathFindingSupplier) {
        List<LocationPair> connections = new ArrayList<>();
        for (int plane = 0; plane < RsRegion.Z; plane++) {
            connections.addAll(filterEdgeConnections(region.getEdgeConnections(0, plane), pathFindingSupplier));
            connections.addAll(filterEdgeConnections(region.getEdgeConnections(1, plane), pathFindingSupplier));
            connections.addAll(filterEdgeConnections(region.getEdgeConnections(2, plane), pathFindingSupplier));
            connections.addAll(filterEdgeConnections(region.getEdgeConnections(3, plane), pathFindingSupplier));
        }
        return connections;
    }

    private List<LocationPair> filterEdgeConnections(List<LocationPair> connections, PathFindingSupplier pathFindingSupplier) {
        List<LocationPair> goodConnections = new ArrayList<>();
        boolean lastConnectsOut = false;
        LocationPair lastPair = null;
        for (LocationPair connection : connections) {
            if (getRegionContaining(connection.getEnd()) == null) {
                continue;
            }

            boolean connectsOut = pathFindingSupplier.isDirectlyConnected(connection.getStart(), connection.getEnd());
            boolean connectsLast = lastPair != null && pathFindingSupplier.isDirectlyConnected(connection.getStart(), lastPair.getStart());
            lastPair = connection;

            if (connectsOut){
                if (!connectsLast || !lastConnectsOut){
                    goodConnections.add(connection);
                    lastConnectsOut = true;
                    continue;
                }
            }

            lastConnectsOut = connectsOut;
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
