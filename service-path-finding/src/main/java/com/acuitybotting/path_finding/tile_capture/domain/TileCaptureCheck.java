package com.acuitybotting.path_finding.tile_capture.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
@Data
@Builder
public class TileCaptureCheck {

    private int x, y, plane;
    private int width, height;
}
