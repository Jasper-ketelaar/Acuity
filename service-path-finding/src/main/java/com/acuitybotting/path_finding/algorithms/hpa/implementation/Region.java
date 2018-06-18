package com.acuitybotting.path_finding.algorithms.hpa.implementation;


import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Region {

    private final Location root;
    private final int width, height;

    public Region(Location root, int width, int height) {
        this.root = root;
        this.width = width;
        this.height = height;
    }

    public Location getRoot() {
        return root;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Region)) return false;

        Region region = (Region) object;

        if (width != region.width) return false;
        if (height != region.height) return false;
        return root != null ? root.equals(region.root) : region.root == null;
    }

    public boolean contains(Location location) {
        return location.getX() >= root.getX() && location.getX() < root.clone(width, 0).getX() &&
                location.getY() >= root.getY() && location.getY() < root.clone(0, height).getY();
    }

    public List<LocationPair> getEdgeConnections(int direction) {
        List<LocationPair> locations = new ArrayList<>();
        if (direction == 0) {
            for (int i = 0; i < width; i++) {
                locations.add(new LocationPair(
                        root.clone(i, 0),
                        root.clone(i, -1)
                ).setCost(1));
            }
        } else if (direction == 1) {
            for (int i = 0; i < width; i++) {
                locations.add(new LocationPair(
                        root.clone(width - 1, i),
                        root.clone(width, i)
                ).setCost(1));
            }
        } else if (direction == 2) {
            for (int i = 0; i < width; i++) {
                locations.add(new LocationPair(
                        root.clone(i, height - 1),
                        root.clone(i, height)
                ).setCost(1));
            }
        } else if (direction == 3) {
            for (int i = 0; i < width; i++) {
                locations.add(new LocationPair(
                        root.clone(0, i),
                        root.clone(-1, i)
                ).setCost(1));
            }
        }
        return locations;
    }

    @Override
    public int hashCode() {
        int result = root != null ? root.hashCode() : 0;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }
}
