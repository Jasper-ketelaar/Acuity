package com.acuitybotting.db.arango.acuity.script.repository.domain;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import com.arangodb.springframework.annotation.Ref;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Collections;
import java.util.Set;

@Document("Script")
@Data
public class Script {

    public static final int ACCESS_PUBLIC = 1;
    public static final int ACCESS_PRIVATE = 2;

    @Id
    private String id;

    @Key
    private String key;

    @Ref
    private AcuityIdentity author;

    private int accessLevel;

    private String category;
    private String title;
    private String description;

    private String githubRepoName;
    private String githubUrl;

    private long creationTime;
    private long lastCompileTime;

    public static Set<String> getCategories(){
        return Collections.singleton("Other");
    }
}
