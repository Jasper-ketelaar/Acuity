package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;


import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.LocationPair;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.PathFindingSupplier;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.Region;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class GraphBuilder {

    private Location lower, upper;
    private int regionWidth, regionHeight;

    private Map<String, Region> regions;

    private int externalConnectionsCount = 0;
    private int internalConnectionCount = 0;

    public Map<String, Region> build(Location lower, Location upper, PathFindingSupplier pathFindingSupplier) {
        this.lower = lower;
        this.upper = upper;

        log.info("Started building HPA graph between {} and {} with width {} and height {}.", lower, upper, regionWidth, regionHeight);
        regions = findRegions();

        log.info("Found {} regions.", regions.size());

        for (Region internalRegion : regions.values()) { //Multi-thread this.
            List<LocationPair> externalConnections = findExternalConnections(internalRegion, pathFindingSupplier);
            for (LocationPair externalConnection : externalConnections) {
                Region externalRegion = getRegionContaining(externalConnection.getEnd()); //Check this is actually always the external region, and isn't null

                HPANode internalNode = new HPANode(internalRegion, externalConnection.getStart());
                HPANode externalNode = new HPANode(externalRegion, externalConnection.getEnd());

                internalNode.addConnection(externalNode);
                externalNode.addConnection(internalNode);

                internalRegion.getNodes().add(internalNode);
                externalRegion.getNodes().add(externalNode);

                externalConnectionsCount++;
            }
        }

        log.info("Found {} external connections.", externalConnectionsCount);

        for (Region region : regions.values()) { //Muli-thread this.
            findInternalConnections(region, pathFindingSupplier);
        }

        log.info("Found {} internal connections.", internalConnectionCount);
        return regions;
    }

    public Region getRegionContaining(Locateable locateable){
        Location offset = locateable.getLocation().subtract(lower);
        int offX = ((int) (offset.getX() / (double) regionWidth)) * regionWidth;
        int offY = ((int) (offset.getY() / (double) regionHeight)) * regionHeight;
        Location base = lower.clone(offX, offY);
        return regions.get(Region.getKey(base.getX(), base.getY(), base.getPlane(), regionWidth, regionHeight));

    }

    private void findInternalConnections(Region region, PathFindingSupplier pathFindingSupplier) {
        for (HPANode startNode : region.getNodes()) {
            for (HPANode endNode : region.getNodes()) {
                if (startNode.equals(endNode)) continue;
                if (startNode.getEdges().stream().anyMatch(edge -> edge.getEnd().equals(endNode))) continue;

                int pathSize = pathFindingSupplier.findPath(
                        startNode.getLocation(),
                        endNode.getLocation(),
                        edge -> limitToRegion(region, edge)
                );

                if (pathSize > 0) {
                    internalConnectionCount++;
                    startNode.addConnection(endNode);
                    endNode.addConnection(startNode);
                }
            }
        }
    }

    private boolean limitToRegion(Region region, Edge edge) {
        if (region == null) return false;
        Node node = edge.getStart();
        if (node == null || node instanceof Locateable && !region.contains(((Locateable) node).getLocation()))
            return false;
        node = edge.getEnd();
        if (node == null || node instanceof Locateable && !region.contains(((Locateable) node).getLocation()))
            return false;
        return true;
    }

    private List<LocationPair> findExternalConnections(Region region, PathFindingSupplier pathFindingSupplier) {
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
            boolean directlyConnected = pathFindingSupplier.isDirectlyConnected(connection.getStart(), connection.getEnd());
            if (lastPairConnected || !directlyConnected) connections.remove(connection);
            lastPairConnected = directlyConnected;
        }

        if (connections.size() > 0) { //Evaluate this.
            Region region = getRegionContaining(connections.get(0).getEnd());
            for (LocationPair connection : new ArrayList<>(connections)) {
                boolean duplicate = connections.stream()
                        .filter(locationPair -> !locationPair.equals(connection))
                        .anyMatch(locationPair -> pathFindingSupplier.isReachableFrom(connection.getEnd(), locationPair.getEnd(), edge -> limitToRegion(region, edge)));
                if (duplicate) connections.remove(connection);
            }
        }

        return connections;
    }

    private Map<String, Region> findRegions() {
        Map<String, Region> regions = new HashMap<>();
        for (int z = lower.getPlane(); z <= upper.getPlane(); z++) {
            for (int x = lower.getX(); x <= upper.getX(); x += regionWidth) {
                for (int y = lower.getY(); y <= upper.getY(); y += regionHeight) {
                    Region region = new Region(new Location(x, y, z), regionWidth, regionHeight);
                    regions.put(region.getKey(), region);
                }
            }
        }
        return regions;
    }

    public GraphBuilder setRegionHeight(int regionHeight) {
        this.regionHeight = regionHeight;
        return this;
    }

    public GraphBuilder setRegionWidth(int regionWidth) {
        this.regionWidth = regionWidth;
        return this;
    }
}
