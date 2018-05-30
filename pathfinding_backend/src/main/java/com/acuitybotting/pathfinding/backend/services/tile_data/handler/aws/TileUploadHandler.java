package com.acuitybotting.pathfinding.backend.services.tile_data.handler.aws;

import com.acuitybotting.pathfinding.backend.services.tile_data.domain.TileCapture;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
public class TileUploadHandler extends SpringBootRequestHandler<TileCapture, String> {
}
