package com.acuitybotting.db.arango.path_finding.domain;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.GeoIndexed;
import com.arangodb.springframework.annotation.Key;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
@Document("TileFlag")
@Builder
@Data
public class TileFlag {

    @Id
    private String id;
    @Key
    private String key;

    @GeoIndexed
    private int[] location;
    private int plane;

    private int flag;

    private String requestID;
}
