package com.acuitybotting.db.arango.path_finding.domain.xtea;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 6/25/2018.
 */
@Getter
@Setter
@Document("RegionInfo")
public class RegionInfo {

    @Id
    private String id;

    @Key
    private String key;

    private byte[][][] renderSettings;
    private int[][][] flags;

}
