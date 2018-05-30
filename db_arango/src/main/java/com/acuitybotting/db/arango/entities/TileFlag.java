package com.acuitybotting.db.arango.entities;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
@Document("TileFlag")
public class TileFlag {

    @Id
    private String id;
    @Key
    private String key;

    private int flag;
}
