package com.acuitybotting.db.arango.path_finding.domain;

import com.acuitybotting.path_finding.rs.utils.CollisionFlags;
import com.acuitybotting.path_finding.rs.utils.Location;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.GeoIndexed;
import com.arangodb.springframework.annotation.Key;
import com.arangodb.springframework.annotation.SkiplistIndexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Indexed;

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
        return CollisionFlags.blockedNorth(getFlag());
    }

    public boolean blockedEast(){
        return CollisionFlags.blockedEast(getFlag());
    }

    public boolean blockedSouth(){
        return CollisionFlags.blockedSouth(getFlag());
    }

    public boolean blockedWest(){
        return CollisionFlags.blockedWest(getFlag());
    }

    public boolean isWalkable(){
        return CollisionFlags.isWalkable(getFlag());
    }

    public boolean isInitialized(){
        return CollisionFlags.isInitialized(getFlag());
    }

    public Location getLocation(){
        return new Location(getX(), getY(), getPlane());
    }
}
