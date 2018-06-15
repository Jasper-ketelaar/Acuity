package com.acuitybotting.db.arango.acuity.identities.domain;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Zachary Herridge on 6/14/2018.
 */
@Document("AcuityIdentity")
@Data
public class AcuityIdentity {

    public static final String PROPERTY_GITHUB_USERNAME = "githubUsername";

    @Id
    private String id;

    @Key
    private String key;

    private String email;
    private String username;

    private String[] principalKeys;

    private Long lastSignInTime;
    private Long creationTime;

    private Map<String, Object> properties;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AcuityIdentity)) return false;
        if (!super.equals(object)) return false;
        AcuityIdentity that = (AcuityIdentity) object;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }
}
