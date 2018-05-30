package com.acuitybotting.db.arango.entities;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.GeoIndexed;
import com.arangodb.springframework.annotation.Key;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
@Document("SceneEntity")
@Builder
@Setter
@Getter
@ToString
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
