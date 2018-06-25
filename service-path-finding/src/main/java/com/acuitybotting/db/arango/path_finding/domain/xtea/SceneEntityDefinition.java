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

    private int ambient;
    private int ambientSoundId;
    private int animation;
    private int clipType;
    private int contrast;
    private int itemSupport;
    private int mapFunction;
    private int mapSceneId;
    private int scaleX;
    private int scaleY;
    private int scaleZ;
    private int sizeX;
    private int sizeY;
    private int translateX;
    private int translateY;
    private int translateZ;
    private int varpIndex;
    private int varpbitIndex;
    private String name;
    private boolean clipped;
    private boolean impenetrable;
    private boolean projectileClipped;
    private boolean rotated;
    private boolean solid;
    private int[] transformIds;
    private String[] actions;
    private short[] colors;
    private short[] newColors;
    private short[] newTextures;
    private short[] textures;
}
