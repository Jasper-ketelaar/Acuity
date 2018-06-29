package com.acuitybotting.path_finding.rs.domain.graph;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.Direction;
import com.acuitybotting.path_finding.rs.utils.MapFlags;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.acuitybotting.path_finding.rs.utils.Direction.*;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */

@Setter
@Getter
public class TileNode implements Node, Locateable {

    private Location location;
    private int type;

    public TileNode(Location location) {
        this.location = location;
    }

    public int getX() {
        return getLocation().getX();
    }

    public int getY() {
        return getLocation().getY();
    }

    public int getPlane() {
        return getLocation().getPlane();
    }

    public Collection<Edge> getNeighbors(boolean ignoreSelf) {
        Set<Edge> edges = new HashSet<>(8);
        boolean north = addEdge(edges, getX(), getY() + 1, getPlane(), NORTH, ignoreSelf);
        boolean east = addEdge(edges, getX() + 1, getY(), getPlane(), EAST, ignoreSelf);
        boolean west = addEdge(edges, getX() - 1, getY(), getPlane(), WEST, ignoreSelf);
        boolean south = addEdge(edges, getX(), getY() - 1, getPlane(), SOUTH, ignoreSelf);

        if (north) {
            if (east) addEdge(edges,getX() + 1, getY() + 1, getPlane(), NORTH_EAST, ignoreSelf);
            if (west) addEdge(edges,getX() - 1, getY() + 1, getPlane(), NORTH_WEST, ignoreSelf);
        }

        if (south) {
            if (east) addEdge(edges, getX() + 1, getY() - 1, getPlane(), SOUTH_EAST, ignoreSelf);
            if (west) addEdge(edges, getX() - 1, getY() - 1, getPlane(), SOUTH_WEST, ignoreSelf);
        }
        return edges;
    }

    @Override
    public Collection<Edge> getNeighbors() {
        return getNeighbors(false);
    }

    private boolean containsDoor(Location location){
        return false;
    }

    private boolean addEdge(Set<Edge> edges, int x, int y, int z, Direction direction, boolean ignoreStartBlocked) {
        Location location = new Location(x, y, z);
        Integer startFlag = RsEnvironment.getRsMap().getFlagAt(new Location(getX(), getY(), getPlane())).orElse(null);
        Integer endFlag = RsEnvironment.getRsMap().getFlagAt(location).orElse(null);

        if (MapFlags.isWalkable(direction, startFlag, endFlag, ignoreStartBlocked)) {
            edges.add(new TileEdge(this, RsEnvironment.getRsMap().getNode(new Location(x, y, z))));
            return true;
        }
        else if (containsDoor(location)){
            TileEdge tileEdge = new TileEdge(this, RsEnvironment.getRsMap().getNode(new Location(x, y, z)), 1);
            tileEdge.setType(HPANode.DOOR);
            edges.add(tileEdge);

            //Possibly return false when contains door to replicate previous performance.
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof TileNode)) return false;
        TileNode tileNode = (TileNode) object;
        return getType() == tileNode.getType() &&
                Objects.equals(getLocation(), tileNode.getLocation());
    }

    @Override
    public int hashCode() {
        return getLocation() != null ? getLocation().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TileNode{" + "location=" + location +
                ", type=" + type +
                '}';
    }
}
