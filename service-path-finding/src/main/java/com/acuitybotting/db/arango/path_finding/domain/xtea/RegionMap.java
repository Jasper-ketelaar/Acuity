package com.acuitybotting.db.arango.path_finding.domain.xtea;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.MapFlags;
import com.arangodb.springframework.annotation.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 6/25/2018.
 */
@Getter
@Setter
@ToString
@Document("RegionMap")
public class RegionMap {

    @Id
    private String id;

    private String key;

    private Integer baseX, baseY;

    private int[][][] flags;

    public void addFlag(int regionX, int regionY, int plane, int flag) {
        flags[plane][regionX][regionY] = MapFlags.add(flags[plane][regionX][regionY], flag);
    }

    public boolean checkFlag(int regionX, int regionY, int plane, int flag) {
        return MapFlags.check(flags[plane][regionX][regionY], flag);
    }

    public void addFlag(Location location, int flag) {
        int regionX = location.getX() - baseX;
        int regionY = location.getY() - baseY;
        addFlag(regionX, regionY, location.getPlane(), flag);
    }
}
