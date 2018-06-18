package com.acuitybotting.path_finding.algorithms.hpa.implementation;


import com.acuitybotting.path_finding.rs.domain.location.Location;

/**
 * Created by Zachary Herridge on 12/1/2017.
 */
public class LocationPair {

    private Location location1, location2;
    private int cost = -1;

    public LocationPair(Location location1, Location location2) {
        this.location1 = location1;
        this.location2 = location2;
    }

    public Location getLocation1() {
        return location1;
    }

    public LocationPair setLocation1(Location location1) {
        this.location1 = location1;
        return this;
    }

    public Location getLocation2() {
        return location2;
    }

    public LocationPair setLocation2(Location location2) {
        this.location2 = location2;
        return this;
    }

    public LocationPair setCost(int cost) {
        this.cost = cost;
        return this;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof LocationPair)) return false;

        LocationPair that = (LocationPair) object;

        if (getLocation1() != null ? !getLocation1().equals(that.getLocation1()) : that.getLocation1() != null)
            return false;
        return getLocation2() != null ? getLocation2().equals(that.getLocation2()) : that.getLocation2() == null;
    }

    @Override
    public int hashCode() {
        int result = getLocation1() != null ? getLocation1().hashCode() : 0;
        result = 31 * result + (getLocation2() != null ? getLocation2().hashCode() : 0);
        return result;
    }
}
