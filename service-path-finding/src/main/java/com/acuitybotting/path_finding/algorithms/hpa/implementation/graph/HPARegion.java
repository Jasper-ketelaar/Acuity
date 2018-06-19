package com.acuitybotting.path_finding.algorithms.hpa.implementation.graph;


import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.domain.location.LocationPair;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class HPARegion {

    private final Location root;
    private final int width, height;

    private final Set<HPANode> nodes = new HashSet<>();

    public HPARegion(Location root, int width, int height) {
        this.root = root;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof HPARegion)) return false;

        HPARegion HPARegion = (HPARegion) object;

        if (width != HPARegion.width) return false;
        if (height != HPARegion.height) return false;
        return root != null ? root.equals(HPARegion.root) : HPARegion.root == null;
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
                ));
            }
        } else if (direction == 1) {
            for (int i = 0; i < width; i++) {
                locations.add(new LocationPair(
                        root.clone(width - 1, i),
                        root.clone(width, i)
                ));
            }
        } else if (direction == 2) {
            for (int i = 0; i < width; i++) {
                locations.add(new LocationPair(
                        root.clone(i, height - 1),
                        root.clone(i, height)
                ));
            }
        } else if (direction == 3) {
            for (int i = 0; i < width; i++) {
                locations.add(new LocationPair(
                        root.clone(0, i),
                        root.clone(-1, i)
                ));
            }
        }
        return locations;
    }

    public String getKey(){
        return getKey(root.getX(), root.getY(), root.getPlane(), getWidth(), getHeight());
    }

    public static String getKey(int baseX, int baseY, int plane, int width, int height){
        return baseX + "_" + baseY + "_" + plane + "_" + width + "_" + height;
    }

    @Override
    public int hashCode() {
        int result = root != null ? root.hashCode() : 0;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }
}
