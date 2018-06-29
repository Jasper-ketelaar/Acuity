package com.acuitybotting.db.arango.path_finding.domain;

import com.acuitybotting.path_finding.rs.utils.RsCollisionFlags;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
@Document("TileFlag")
@Data
public class TileFlag {

    @Id
    private String id;
    @Key
    private String key;

    private Boolean deprecated;

    private int x;
    private int y;
    private int plane;

    private int flag;

    private String requestID;

    public boolean blockedNorth(){
        return RsCollisionFlags.blockedNorth(getFlag());
    }

    public boolean blockedEast(){
        return RsCollisionFlags.blockedEast(getFlag());
    }

    public boolean blockedSouth(){
        return RsCollisionFlags.blockedSouth(getFlag());
    }

    public boolean blockedWest(){
        return RsCollisionFlags.blockedWest(getFlag());
    }

    public boolean isWalkable(){
        return RsCollisionFlags.isWalkable(getFlag());
    }

    public boolean isInitialized(){
        return RsCollisionFlags.isInitialized(getFlag());
    }

    public Location getLocation(){
        return new Location(getX(), getY(), getPlane());
    }
}
