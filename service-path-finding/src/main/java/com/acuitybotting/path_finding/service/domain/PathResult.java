package com.acuitybotting.path_finding.service.domain;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PathResult {

    @Expose
    private String error;

    @Expose
    private List<Edge> path;

}
