package com.acuitybotting.arango.acuity.script.repository.domain;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Document("Script")
@Data
public class Script {

    @Id
    private String id;

    @Key
    private String key;

    private String githubUrl;

}
