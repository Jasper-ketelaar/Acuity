package com.acuitybotting.pathfinding.backend.services.tile_data.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
@Setter
@Getter
@Builder
public class TileUpload {

    private int x, y, plane;
    private int[][] flags;
}
