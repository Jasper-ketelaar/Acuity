package com.acuitybotting.path_finding.domain.hpa;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
@Getter
@Setter
public class SavedNode {

    private String key;

    private Integer webVersion;

    private String regionKey;
    private List<String> edgeKeys;
    private Location location;
    private Integer type;

}
