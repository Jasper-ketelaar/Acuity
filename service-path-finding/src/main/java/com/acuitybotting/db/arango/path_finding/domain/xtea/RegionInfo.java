package com.acuitybotting.db.arango.path_finding.domain.xtea;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.RsMapService;
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
        Location location = RsMapService.regionIdToBase(Integer.parseInt(key));
        baseX = location.getX();
        baseY = location.getY();
    }
}
