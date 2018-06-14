package com.acuitybotting.db.arango.acuity.script.repository.domain;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import com.arangodb.springframework.annotation.Ref;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Document("Script")
@Data
public class Script {

    @Id
    private String id;

    @Key
    private String key;

    @Ref
    private AcuityIdentity author;

    private String githubUrl;
}
