package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class TerminatingEdge extends HPAEdge{

    private String pathKey;
    private List<Location> path;

    public TerminatingEdge(HPANode start, HPANode end) {
        super(start, end);
    }

    public void setPath(List<Edge> path, boolean reverse) {
        pathKey = UUID.randomUUID().toString();
        this.path = path.stream().map(edge -> {
            if (edge.getEnd() instanceof Locateable) return ((Locateable) edge.getEnd()).getLocation();
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (reverse) Collections.reverse(path);
    }

    @Override
    public String getPathKey() {
        return pathKey;
    }

    @Override
    public List<Location> getPath() {
        return path;
    }
}
