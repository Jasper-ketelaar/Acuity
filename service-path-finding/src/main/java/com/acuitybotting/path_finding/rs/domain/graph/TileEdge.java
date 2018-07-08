package com.acuitybotting.path_finding.rs.domain.graph;

import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */
@Setter
@Getter
public class TileEdge implements Edge {

    @Expose
    private TileNode start, end;

    private double costPenalty = 0;

    @Expose
    private int type;

    public TileEdge(TileNode start, TileNode end) {
        this.start = start;
        this.end = end;
    }

    public TileEdge(TileNode start, TileNode end, double costPenalty) {
        this.start = start;
        this.end = end;
        this.costPenalty = costPenalty;
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
    public double getCostPenalty(){
        return costPenalty;
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
        int result = getStart() != null ? getStart().hashCode() : 0;
        result = 31 * result + (getEnd() != null ? getEnd().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TileEdge{");
        sb.append("start=").append(start);
        sb.append(", end=").append(end);
        sb.append('}');
        return sb.toString();
    }
}
