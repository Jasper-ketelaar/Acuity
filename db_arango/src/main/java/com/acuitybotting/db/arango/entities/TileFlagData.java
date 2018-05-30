package com.acuitybotting.db.arango.entities;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.GeoIndexed;
import com.arangodb.springframework.annotation.Key;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
@Document("TileFlag")
@Builder
@ToString
@Data
public class TileFlagData {

    @Id
    private String id;
    @Key
    private String key;

    @GeoIndexed
    private int[] location;
    private int plane;

    private int flag;

    private String requestID;
}
