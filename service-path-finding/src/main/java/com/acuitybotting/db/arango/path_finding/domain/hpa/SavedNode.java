package com.acuitybotting.db.arango.path_finding.domain.hpa;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
@Document("HpaNode")
@Getter
@Setter
public class SavedNode {

    @Id
    private String id;

    private String key;

    private Integer webVersion;

    private String regionKey;
    private List<String> edgeKeys;
    private Location location;
    private Integer type;

}
