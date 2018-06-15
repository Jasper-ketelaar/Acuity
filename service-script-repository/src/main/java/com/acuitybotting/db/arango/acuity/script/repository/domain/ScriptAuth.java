package com.acuitybotting.db.arango.acuity.script.repository.domain;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import lombok.Data;

/**
 * Created by Zachary Herridge on 6/15/2018.
 */
@Data
@Document("ScriptAuth")
public class ScriptAuth {

    public static final int PAID = 1;
    public static final int PRIVATE_SCRIPT = 2;

    private long creationTime;
    private long expirationTime;

    private int authType;

    @Ref
    private Script script;

    @Ref
    private AcuityIdentity principal;

    public boolean isActive(){
        return System.currentTimeMillis() < expirationTime;
    }
}
