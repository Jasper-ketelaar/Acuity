package com.acuitybotting.path_finding.domain.hpa;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
@Getter
@Setter
public class SavedRegion {

    private String key;

    private Integer webVersion;

    private Location root;
    private Integer width, height;

    private Map<String, String> nodes = new HashMap<>();

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
