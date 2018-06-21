package com.acuitybotting.db.arango.path_finding.domain.hpa;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.domain.location.LocationPair;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
@Document("SavedHpaEdge")
@Getter
@Setter
public class SavedEdge {

    @Id
    private String id;
    @Key
    private String key;

    private int webVersion;

    private String startKey, endKey;
    private double cost;

    private List<Location> path;
}
