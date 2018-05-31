package com.acuitybotting.pathfinding.backend.services.tile_data.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
@Data
@Builder
public class TileCaptureCheck {

    private int x, y, z;
    private int width, height;
}
