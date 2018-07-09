package com.acuitybotting.db.arango.path_finding.domain.xtea;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.Arrays;

@Getter
@ToString
@Document("SceneEntityDefinition")
public class SceneEntityDefinition {

    @Id
    private String id;

    @Key
    private String key;

    private Integer mapDoorFlag;
    private Integer objectId;
    private Integer ambient;
    private Integer ambientSoundId;
    private Integer animation;
    private Integer clipType;
    private Integer contrast;
    private Integer itemSupport;
    private Integer mapFunction;
    private Integer mapSceneId;
    private Integer scaleX;
    private Integer scaleY;
    private Integer scaleZ;
    private Integer sizeX;
    private Integer sizeY;
    private Integer translateX;
    private Integer translateY;
    private Integer translateZ;
    private Integer varpIndex;
    private Integer varpbitIndex;
    private String name;
    private Boolean clipped;
    private Boolean impenetrable;
    private Boolean projectileClipped;
    private Boolean rotated;
    private Boolean solid;
    private int[] transformIds;
    private String[] actions;
    private int[] colors;
    private int[] newColors;
    private int[] newTextures;
    private int[] textures;
}
