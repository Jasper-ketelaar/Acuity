package com.acuitybotting.path_finding.rs.domain;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.rs.utils.RsEnvironmentService;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */
@Setter
@Getter
public class TileEdge implements Edge{

    private TileNode start, end;

    public TileEdge(TileNode start, TileNode end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Node getStart() {
        return start;
    }

    @Override
    public Node getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof TileEdge)) return false;
        TileEdge tileEdge = (TileEdge) object;
        return Objects.equals(getStart(), tileEdge.getStart()) &&
                Objects.equals(getEnd(), tileEdge.getEnd());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getStart() != null ? getStart().hashCode() : 0);
        result = 31 * result + (getEnd() != null ? getEnd().hashCode() : 0);
        return result;
    }
}
