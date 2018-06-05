package com.acuitybotting.db.arango.path_finding.domain;

import com.acuitybotting.path_finding.rs.utils.CollisionFlags;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.GeoIndexed;
import com.arangodb.springframework.annotation.Key;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
@Document("TileFlag")
@Builder
@Data
public class TileFlag {

    @Id
    private String id;
    @Key
    private String key;

    @GeoIndexed
    private int[] location;
    private int plane;

    private int flag;

    private String requestID;

    public boolean blockedNorth(){
        return blockedNorth(getFlag());
    }

    public boolean blockedEast(){
        return blockedEast(getFlag());
    }
    public boolean blockedSouth(){
        return blockedSouth(getFlag());
    }
    public boolean blockedWest(){
        return blockedWest(getFlag());
    }

    public boolean isWalkable(){
        return isWalkable(getFlag());
    }

    public boolean isInitialized(){
        return isInitialized(getFlag());
    }

    public static boolean blockedNorth(int collisionData){
        return CollisionFlags.check(collisionData, CollisionFlags.NORTH_WALL) || CollisionFlags.check(collisionData, CollisionFlags.BLOCKED_NORTH_WALL);
    }

    public static boolean blockedEast(int collisionData){
        return CollisionFlags.check(collisionData, CollisionFlags.EAST_WALL) || CollisionFlags.check(collisionData, CollisionFlags.BLOCKED_EAST_WALL);
    }
    public static boolean blockedSouth(int collisionData){
        return CollisionFlags.check(collisionData, CollisionFlags.SOUTH_WALL) || CollisionFlags.check(collisionData, CollisionFlags.BLOCKED_SOUTH_WALL);
    }
    public static boolean blockedWest(int collisionData){
        return CollisionFlags.check(collisionData, CollisionFlags.WEST_WALL) || CollisionFlags.check(collisionData, CollisionFlags.BLOCKED_WEST_WALL);
    }

    public static boolean isWalkable(int collisionData){
        return !(CollisionFlags.check(collisionData, CollisionFlags.OCCUPIED)
                || CollisionFlags.check(collisionData, CollisionFlags.SOLID)
                || CollisionFlags.check(collisionData, CollisionFlags.BLOCKED)
                || CollisionFlags.check(collisionData, CollisionFlags.CLOSED));
    }

    public static boolean isInitialized(int collisionData){
        return !(blockedNorth(collisionData) && blockedEast(collisionData) && blockedSouth(collisionData) && blockedWest(collisionData) && !isWalkable(collisionData)) || CollisionFlags.check(collisionData, CollisionFlags.INITIALIZED);
    }
}
