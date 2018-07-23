package com.acuitybotting.path_finding.domain.hpa;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Zachary Herridge on 6/29/2018.
 */
@Getter
@Setter
public class SavedPath {

    private String key;

    private String edgeKey;
    private Integer webVersion;
    private List<Location> path;
}
