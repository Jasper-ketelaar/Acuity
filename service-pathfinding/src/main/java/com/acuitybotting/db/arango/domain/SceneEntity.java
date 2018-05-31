package com.acuitybotting.db.arango.domain;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.GeoIndexed;
import com.arangodb.springframework.annotation.Key;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
@Document("SceneEntity")
@Builder
@Data
public class SceneEntity {

    @Id
    private String id;
    @Key
    private String key;

    private String name;
    private String entityID;

    private int plane;
    @GeoIndexed
    private int[] location;

    private String[] actions;
}
