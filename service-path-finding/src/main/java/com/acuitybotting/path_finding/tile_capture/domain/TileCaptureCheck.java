package com.acuitybotting.path_finding.tile_capture.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
@Data
public class TileCaptureCheck {

    private int x, y, plane;
    private int width, height;

}
