package com.acuitybotting.path_finding.service.domain;

import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class PathResult {

    @Expose
    private String error;

    @Expose
    private List<Edge> path;

    @Expose
    private Map<String, List<Location>> subPaths;

    private AStarImplementation aStarImplementation;
}
