package com.acuitybotting.db.arango.path_finding.domain.hpa;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
@Document("HpaEdge")
@Getter
@Setter
public class SavedEdge {

    @Id
    private String id;


    private String key;

    private Integer webVersion;

    private String startKey, endKey;
    private String pathKey;
    private Double cost;
    private Integer type;
}
