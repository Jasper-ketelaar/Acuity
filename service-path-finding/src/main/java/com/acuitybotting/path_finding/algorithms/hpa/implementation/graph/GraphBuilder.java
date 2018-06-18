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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
public class GraphBuilder {

    private int regionWidth, regionHeight;

    private Set<Region> regions;
    private List<LocationPair> connections = new CopyOnWriteArrayList<>();

    public Map<Location, HPANode> build(Location lower, Location upper, PathFindingSupplier pathFindingSupplier) {
        log.info("Started building HPA graph between {} and {}.", lower, upper);
        regions = findRegions(lower, upper);

        log.info("Found {} regions.", regions.size());

        regions.stream().parallel().forEach(region -> {
            List<LocationPair> outgoingConnections = findExternalConnections(region, pathFindingSupplier);
            connections.addAll(outgoingConnections);
        });

        log.info("Found {} external connections.", connections.size());

        regions.parallelStream().forEach(region -> {
            Set<Location> internalLocations = connections.parallelStream()
                    .map(locationPair -> {
                        if (region.contains(locationPair.getLocation1())) return locationPair.getLocation1();
                        if (region.contains(locationPair.getLocation2())) return locationPair.getLocation2();
                        return null;
                    })
                    .filter(Objects::nonNull).collect(Collectors.toSet());

            List<LocationPair> innerConnections = findInnerConnections(region, internalLocations, pathFindingSupplier);
            connections.addAll(innerConnections);
        });

        Map<Location, HPANode> graph = buildNodes(connections);

        log.info("Finished building HPA graph with {} nodes.", graph.size());

        return graph;
    }

    private Map<Location, HPANode> buildNodes(List<LocationPair> nodeCollection) {
        Map<Location, HPANode> nodes = new HashMap<>();
        nodeCollection.forEach(locationPair -> {
            HPANode node1 = nodes.computeIfAbsent(locationPair.getLocation1(), location -> new HPANode(location).setRegion(findRegionContaining(location)));
            HPANode node2 = nodes.computeIfAbsent(locationPair.getLocation2(), location -> new HPANode(location).setRegion(findRegionContaining(location)));
            node1.getNeighbors().add(new HPAEdge(node1, node2));
            node2.getNeighbors().add(new HPAEdge(node2, node1));
        });
        return nodes;
    }

    private List<LocationPair> findInnerConnections(Region region, Set<Location> intneralLocations, PathFindingSupplier pathFindingSupplier) {
        List<LocationPair> connections = new CopyOnWriteArrayList<>();
        intneralLocations.forEach(c1 -> {
            intneralLocations.stream().parallel()
                    .filter(location -> !c1.equals(location))
                    .sorted(Comparator.comparingDouble(o -> o.getTraversalCost(c1)))
                    .map(location -> {
                        int pathLength = pathFindingSupplier.findPath(c1, location, edge -> limitToRegion(region, edge));
                        return new LocationPair(c1, location).setCost(pathLength);
                    })
                    .filter(locationPair -> locationPair.getCost() > 0)
                    .limit(3)
                    .forEach(connections::add);
        });
        return connections;
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

    public Region findRegionContaining(Location location) {
        return regions.stream().filter(region -> region.contains(location)).findAny().orElse(null);
    }

    private List<LocationPair> filterEdgeConnections(List<LocationPair> connections, PathFindingSupplier pathFindingSupplier) {
        boolean lastPairConnected = false;
        for (LocationPair connection : new ArrayList<>(connections)) {
            boolean directlyConnected = pathFindingSupplier.isDirectlyConnected(connection.getLocation1(), connection.getLocation2());
            if (lastPairConnected || !directlyConnected) connections.remove(connection);
            lastPairConnected = directlyConnected;
        }

        if (connections.size() > 0) {
            Region region = findRegionContaining(connections.get(0).getLocation2());
            for (LocationPair connection : new ArrayList<>(connections)) {
                boolean duplicate = connections.stream()
                        .filter(locationPair -> !locationPair.equals(connection))
                        .anyMatch(locationPair -> pathFindingSupplier.isReachableFrom(connection.getLocation2(), locationPair.getLocation2(), edge -> limitToRegion(region, edge)));
                if (duplicate) connections.remove(connection);
            }
        }

        return connections;
    }

    private Set<Region> findRegions(Location lower, Location upper) {
        Set<Region> regions = new HashSet<>();
        for (int z = lower.getPlane(); z <= upper.getPlane(); z++) {
            for (int x = lower.getX(); x <= upper.getX(); x += regionWidth) {
                for (int y = lower.getY(); y <= upper.getY(); y += regionHeight) {
                    regions.add(new Region(new Location(x, y, z), regionWidth, regionHeight));
                }
            }
        }
        return regions;
    }

    public Set<Region> getRegions() {
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
