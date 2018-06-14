package com.acuitybotting.db.arango.acuity.identities.domain;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 6/14/2018.
 */
@Document("AcuityIdentity")
@Data
public class AcuityIdentity {

    @Id
    private String id;

    @Key
    private String key;

    private String email;
    private String username;

    private String[] principalKeys;
}
