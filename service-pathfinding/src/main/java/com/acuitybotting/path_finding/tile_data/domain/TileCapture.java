package com.acuitybotting.path_finding.tile_data.domain;

import com.acuitybotting.db.arango.domain.SceneEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
@Data
@Builder
public class TileCapture {

    private int x, y, plane;
    private int[][] flags;
    private List<SceneEntity> entities;

}
