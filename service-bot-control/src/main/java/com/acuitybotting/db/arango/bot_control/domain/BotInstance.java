package com.acuitybotting.db.arango.bot_control.domain;

import com.arangodb.springframework.annotation.Document;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Document("BotInstance")
@Data
public class BotInstance {

    @Id
    private String id;

    private long lastHeartbeat;

}
