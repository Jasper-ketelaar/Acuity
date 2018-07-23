package com.acuitybotting.db.arango.path_finding.domain.hpa;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.domain.location.LocationPair;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Created by Zachary Herridge on 6/29/2018.
 */
@Document("HpaPath")
@Getter
@Setter
public class SavedPath {

    @Id
    private String id;

    private String key;

    private String edgeKey;
    private Integer webVersion;
    private List<Location> path;
}
