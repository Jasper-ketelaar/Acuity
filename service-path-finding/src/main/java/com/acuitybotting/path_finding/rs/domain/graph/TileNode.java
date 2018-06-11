package com.acuitybotting.path_finding.rs.domain.graph;

import com.acuitybotting.db.arango.path_finding.domain.SceneEntity;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.CollisionFlags;
import com.acuitybotting.path_finding.rs.utils.Direction;
import com.acuitybotting.path_finding.rs.utils.RsEnvironmentService;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.acuitybotting.path_finding.rs.utils.Direction.*;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */

@Setter
@Getter
public class TileNode implements Node, Locateable {

    public static final int WALK = 1;
    public static final int DOOR = 2;
    
    private Location location;
    private int type;

    public TileNode(Location location) {
        this.location = location;
        this.type = WALK;
    }

    public TileNode(Location location, int type) {
        this.location = location;
        this.type = type;
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

    @Override
    public Collection<Edge> getNeighbors() {
        Set<Edge> edges = new HashSet<>(8);

        Optional<TileNode> north = getEdge(getX(), getY() + 1, getPlane(), NORTH);
        north.ifPresent(aStarLocation -> edges.add(new TileEdge(this, aStarLocation)));
        getDoor(getX(), getY() + 1, getPlane(), NORTH).ifPresent(aStarLocation -> edges.add(new TileEdge(this, aStarLocation)));

        Optional<TileNode> east = getEdge(getX() + 1, getY(), getPlane(), EAST);
        east.ifPresent(aStarLocation -> edges.add(new TileEdge(this, aStarLocation)));
        getDoor(getX() + 1, getY(), getPlane(), EAST).ifPresent(aStarLocation -> edges.add(new TileEdge(this, aStarLocation)));

        Optional<TileNode> west = getEdge(getX() - 1, getY(), getPlane(), WEST);
        west.ifPresent(aStarLocation -> edges.add(new TileEdge(this, aStarLocation)));
        getDoor(getX() - 1, getY(), getPlane(), WEST).ifPresent(aStarLocation -> edges.add(new TileEdge(this, aStarLocation)));

        Optional<TileNode> south = getEdge(getX(), getY() - 1, getPlane(), SOUTH);
        south.ifPresent(aStarLocation -> edges.add(new TileEdge(this, aStarLocation)));
        getDoor(getX(), getY() - 1, getPlane(), SOUTH).ifPresent(aStarLocation -> edges.add(new TileEdge(this, aStarLocation)));

        if (north.isPresent()) {
            if (east.isPresent())
                getEdge(getX() + 1, getY() + 1, getPlane(), NORTH_EAST).ifPresent(aStarLocation -> edges.add(new TileEdge(this, aStarLocation)));
            if (west.isPresent())
                getEdge(getX() - 1, getY() + 1, getPlane(), NORTH_WEST).ifPresent(aStarLocation -> edges.add(new TileEdge(this, aStarLocation)));
        }

        if (south.isPresent()) {
            if (east.isPresent())
                getEdge(getX() + 1, getY() - 1, getPlane(), SOUTH_EAST).ifPresent(aStarLocation -> edges.add(new TileEdge(this, aStarLocation)));
            if (west.isPresent())
                getEdge(getX() - 1, getY() - 1, getPlane(), SOUTH_WEST).ifPresent(aStarLocation -> edges.add(new TileEdge(this, aStarLocation)));
        }

        return edges;
    }

    private Optional<TileNode> getDoor(int x, int y, int z, Direction dir) {
        if (getDoor(new Location(getX(), getY(), getPlane())).orElse(null) != null) {
            return Optional.of(RsEnvironmentService.getRsEnvironment().getNode(new Location(x, y, z), DOOR));
        }

        if (getDoor(new Location(x, y, z)).orElse(null) != null) {
            return Optional.of(RsEnvironmentService.getRsEnvironment().getNode(new Location(x, y, z), DOOR));
        }

        return Optional.empty();
    }

    private Optional<SceneEntity> getDoor(Location location) {
        List<SceneEntity> doors = RsEnvironmentService.getRsEnvironment().getDoorsAt(location);
        if (doors.size() > 0) {
            return Optional.of(doors.get(0));
        }
        return Optional.empty();
    }

    private Optional<TileNode> getEdge(int x, int y, int z, Direction dir) {
        Integer localFlag = RsEnvironmentService.getRsEnvironment().getFlagAt(new Location(getX(), getY(), getPlane()));
        Integer flag = RsEnvironmentService.getRsEnvironment().getFlagAt(new Location(x, y, z));

        if (CollisionFlags.checkWalkable(dir, localFlag, flag)) {
            return Optional.of(RsEnvironmentService.getRsEnvironment().getNode(new Location(x, y, z)));
        }

        return Optional.empty();
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
        final StringBuilder sb = new StringBuilder("TileNode{");
        sb.append("location=").append(location);
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
