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

    private Integer baseX, baseY;

    private int[][][] renderSettings;
    private int[][][] flags;
    private int[][][] doors;

    public void init(){
        baseX = ((Integer.parseInt(key) >> 8) & 0xFF) << 6;
        baseY = (Integer.parseInt(key) & 0xFF) << 6;
    }
}
