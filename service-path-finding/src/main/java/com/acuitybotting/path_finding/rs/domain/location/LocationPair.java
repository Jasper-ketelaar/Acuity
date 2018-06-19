package com.acuitybotting.path_finding.rs.domain.location;


public class LocationPair {

    private Location start, end;

    public LocationPair(Location start, Location end) {
        this.start = start;
        this.end = end;
    }

    public Location getStart() {
        return start;
    }

    public LocationPair setStart(Location start) {
        this.start = start;
        return this;
    }

    public Location getEnd() {
        return end;
    }

    public LocationPair setEnd(Location end) {
        this.end = end;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof LocationPair)) return false;

        LocationPair that = (LocationPair) object;

        if (getStart() != null ? !getStart().equals(that.getStart()) : that.getStart() != null)
            return false;
        return getEnd() != null ? getEnd().equals(that.getEnd()) : that.getEnd() == null;
    }

    @Override
    public int hashCode() {
        int result = getStart() != null ? getStart().hashCode() : 0;
        result = 31 * result + (getEnd() != null ? getEnd().hashCode() : 0);
        return result;
    }

    public LocationPair reverse() {
        return new LocationPair(getEnd(), getStart());
    }
}
