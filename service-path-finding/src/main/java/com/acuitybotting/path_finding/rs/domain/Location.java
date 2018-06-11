package com.acuitybotting.path_finding.rs.domain;

import com.acuitybotting.path_finding.rs.utils.RsEnvironmentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Location implements Locateable{

    private int x, y, plane;

    public double getTraversalCost(Location other) {
        return  Math.abs(getX() - other.getX()) + Math.abs(getY() - other.getY()) + (getPlane() != (other).getPlane() ? RsEnvironmentService.PLANE_PENALTY : 0);
    }

    @Override
    public Location getLocation() {
        return this;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Location)) return false;
        Location location = (Location) object;
        return getX() == location.getX() &&
                getY() == location.getY() &&
                getPlane() == location.getPlane();
    }

    @Override
    public int hashCode() {
        int result = getX();
        result = 31 * result + getY();
        result = 31 * result + getPlane();
        return result;
    }
}
