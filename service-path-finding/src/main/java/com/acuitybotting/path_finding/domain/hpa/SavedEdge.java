package com.acuitybotting.path_finding.domain.hpa;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
@Getter
@Setter
public class SavedEdge {

    private String key;

    private Integer webVersion;

    private String startKey, endKey;
    private String pathKey;
    private Double cost;
    private Integer type;
}
