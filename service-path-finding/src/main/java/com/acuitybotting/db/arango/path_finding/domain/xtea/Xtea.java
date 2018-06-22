package com.acuitybotting.db.arango.path_finding.domain.xtea;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 6/22/2018.
 */
@Getter
@Setter
@Document("RegionXtea")
public class Xtea {

    @Id
    private String id;

    @Key
    private String key;

    private int revision;
    private int region;
    private int[] keys;
}
