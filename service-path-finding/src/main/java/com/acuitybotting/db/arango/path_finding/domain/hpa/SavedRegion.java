package com.acuitybotting.db.arango.path_finding.domain.hpa;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
@Document("SavedHpaRegion")
@Getter
@Setter
public class SavedRegion {

    @Id
    private String id;
    @Key
    private String key;

    private int webVersion;

    private Location root;
    private int width, height;

    private Map<Location, String> nodes = new HashMap<>();

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof SavedRegion)) return false;
        if (!super.equals(object)) return false;
        SavedRegion that = (SavedRegion) object;
        return getWidth() == that.getWidth() &&
                getHeight() == that.getHeight() &&
                Objects.equals(getRoot(), that.getRoot());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoot(), getWidth(), getHeight());
    }
}
