package com.acuitybotting.db.arango.bot_control.domain;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
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

    @Key
    private String key;

    private String realm;
    private String principal;

    private String authKey;

    private String queueUrl;
    private long lastHeartbeat;


}
