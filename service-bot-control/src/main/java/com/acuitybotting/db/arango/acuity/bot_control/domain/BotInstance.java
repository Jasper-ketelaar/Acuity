package com.acuitybotting.db.arango.acuity.bot_control.domain;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Document("BotInstance")
@Getter
@Setter
@ToString
public class BotInstance {

    @Id
    private String id;

    @Key
    private String key;

    private String principalKey;

    private String queueUrl;
    private String queueAuth;

    private long connectionTime;
    private long lastHeartbeatTime;

    private Map<String, Object> attributes = new HashMap<>();
}
