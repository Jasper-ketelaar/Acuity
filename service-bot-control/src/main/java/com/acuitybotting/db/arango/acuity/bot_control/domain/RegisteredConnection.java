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
@Document("RegisteredConnection")
@Getter
@Setter
@ToString
public class RegisteredConnection {

    @Id
    private String id;

    private String principalKey;

    private String connectionId;
    private String connectionType;

    private long connectionTime;
    private long lastHeartbeatTime;

    private Map<String, String> attributes;
}
