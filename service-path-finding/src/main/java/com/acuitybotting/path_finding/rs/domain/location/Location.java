package com.acuitybotting.path_finding.rs.domain.location;

import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Location implements Locateable{

    private int x, y, plane;

    public double getTraversalCost(Location other) {
        return  Math.abs(getX() - other.getX()) + Math.abs(getY() - other.getY()) + (getPlane() != (other).getPlane() ? RsEnvironment.PLANE_PENALTY : 0);
    }

    @Override
    public Location getLocation() {
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Location{");
        sb.append("x=").append(x);
        sb.append(", y=").append(y);
        sb.append(", plane=").append(plane);
        sb.append('}');
        return sb.toString();
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
