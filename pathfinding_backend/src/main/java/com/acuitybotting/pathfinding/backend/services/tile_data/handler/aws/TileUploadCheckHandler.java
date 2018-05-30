package com.acuitybotting.pathfinding.backend.services.tile_data.handler.aws;

import com.acuitybotting.pathfinding.backend.services.tile_data.domain.TileCaptureCheck;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
public class TileUploadCheckHandler extends SpringBootRequestHandler<TileCaptureCheck, String> {

}
