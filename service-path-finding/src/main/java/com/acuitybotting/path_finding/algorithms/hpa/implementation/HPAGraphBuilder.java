package com.acuitybotting.path_finding.algorithms.hpa.implementation;


import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.rs.domain.location.LocationPair;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class HPAGraphBuilder {

    private Location lower, upper;
    private int regionWidth, regionHeight;

    private Map<String, HPARegion> regions;

    private int externalConnectionsCount = 0;
    private int internalConnectionCount = 0;

    public Map<String, HPARegion> build(Location lower, Location upper, PathFindingSupplier pathFindingSupplier) {
        this.lower = lower;
        this.upper = upper;

        log.info("Started building HPA graph between {} and {} with width {} and height {}.", lower, upper, regionWidth, regionHeight);
        regions = findRegions();

        log.info("Found {} regions.", regions.size());

        for (HPARegion internalHPARegion : regions.values()) { //Multi-thread this.
            List<LocationPair> externalConnections = findExternalConnections(internalHPARegion, pathFindingSupplier);
            for (LocationPair externalConnection : externalConnections) {
                HPARegion externalHPARegion = getRegionContaining(externalConnection.getEnd()); //Check this is actually always the external region, and isn't null

                HPANode internalNode = new HPANode(internalHPARegion, externalConnection.getStart());
                HPANode externalNode = new HPANode(externalHPARegion, externalConnection.getEnd());

                internalNode.addConnection(externalNode, 1);
                externalNode.addConnection(internalNode, 1);

                internalHPARegion.getNodes().add(internalNode);
                externalHPARegion.getNodes().add(externalNode);

                externalConnectionsCount++;
            }
        }

        log.info("Found {} external connections.", externalConnectionsCount);

        for (HPARegion HPARegion : regions.values()) { //Muli-thread this.
            findInternalConnections(HPARegion, pathFindingSupplier);
        }

        log.info("Found {} internal connections.", internalConnectionCount);
        return regions;
    }

    public HPARegion getRegionContaining(Locateable locateable){
        Location offset = locateable.getLocation().subtract(lower);
        int offX = ((int) (offset.getX() / (double) regionWidth)) * regionWidth;
        int offY = ((int) (offset.getY() / (double) regionHeight)) * regionHeight;
        Location base = lower.clone(offX, offY);
        return regions.get(HPARegion.getKey(base.getX(), base.getY(), base.getPlane(), regionWidth, regionHeight));

    }

    private void findInternalConnections(HPARegion HPARegion, PathFindingSupplier pathFindingSupplier) {
        for (HPANode startNode : HPARegion.getNodes()) {
            for (HPANode endNode : HPARegion.getNodes()) {
                if (startNode.equals(endNode)) continue;
                if (startNode.getEdges().stream().anyMatch(edge -> edge.getEnd().equals(endNode))) continue;

                int pathSize = pathFindingSupplier.findPath(
                        startNode.getLocation(),
                        endNode.getLocation(),
                        edge -> limitToRegion(HPARegion, edge)
                );

                if (pathSize > 0) {
                    internalConnectionCount++;
                    startNode.addConnection(endNode, pathSize);
                    endNode.addConnection(startNode, pathSize);
                }
            }
        }
    }

    private boolean limitToRegion(HPARegion HPARegion, Edge edge) {
        if (HPARegion == null) return false;
        Node node = edge.getStart();
        if (node == null || node instanceof Locateable && !HPARegion.contains(((Locateable) node).getLocation()))
            return false;
        node = edge.getEnd();
        if (node == null || node instanceof Locateable && !HPARegion.contains(((Locateable) node).getLocation()))
            return false;
        return true;
    }

    private List<LocationPair> findExternalConnections(HPARegion HPARegion, PathFindingSupplier pathFindingSupplier) {
        List<LocationPair> connections = new ArrayList<>();
        connections.addAll(filterEdgeConnections(HPARegion.getEdgeConnections(0), pathFindingSupplier));
        connections.addAll(filterEdgeConnections(HPARegion.getEdgeConnections(1), pathFindingSupplier));
        connections.addAll(filterEdgeConnections(HPARegion.getEdgeConnections(2), pathFindingSupplier));
        connections.addAll(filterEdgeConnections(HPARegion.getEdgeConnections(3), pathFindingSupplier));
        return connections;
    }

    private List<LocationPair> filterEdgeConnections(List<LocationPair> connections, PathFindingSupplier pathFindingSupplier) {
        boolean lastPairConnected = false;
        for (LocationPair connection : new ArrayList<>(connections)) {
            if (getRegionContaining(connection.getEnd()) == null){ // Evaluate this.
                connections.remove(connection);
                continue;
            }

            boolean directlyConnected = pathFindingSupplier.isDirectlyConnected(connection.getStart(), connection.getEnd());
            if (lastPairConnected || !directlyConnected) connections.remove(connection);
            lastPairConnected = directlyConnected;
        }

        if (connections.size() > 1) { //Evaluate this.
            HPARegion HPARegion = getRegionContaining(connections.get(0).getEnd());
            for (LocationPair connection : new ArrayList<>(connections)) {
                boolean duplicate = connections.stream()
                        .filter(locationPair -> !locationPair.equals(connection))
                        .anyMatch(locationPair -> pathFindingSupplier.isReachableFrom(connection.getEnd(), locationPair.getEnd(), edge -> limitToRegion(HPARegion, edge)));
                if (duplicate) connections.remove(connection);
            }
        }

        return connections;
    }

    private Map<String, HPARegion> findRegions() {
        Map<String, HPARegion> regions = new HashMap<>();
        for (int z = lower.getPlane(); z <= upper.getPlane(); z++) {
            for (int x = lower.getX(); x <= upper.getX(); x += regionWidth) {
                for (int y = lower.getY(); y <= upper.getY(); y += regionHeight) {
                    HPARegion HPARegion = new HPARegion(new Location(x, y, z), regionWidth, regionHeight);
                    regions.put(HPARegion.getKey(), HPARegion);
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
}
