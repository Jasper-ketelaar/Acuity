package com.acuitybotting.pathfinding.backend.services.tile_data.handler.aws;

import com.acuitybotting.db.arango.entities.TileFlagData;
import com.acuitybotting.db.arango.repositories.TileFlagRepository;
import com.acuitybotting.pathfinding.backend.services.tile_data.domain.TileUpload;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.geo.Polygon;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Created by Zachary Herridge on 5/29/2018.
 */
@ComponentScan("com.acuitybotting.db.arango.repositories")
public class TileUploadHandler implements RequestHandler<TileUpload, String> {

    @Autowired
    private TileFlagRepository tileFlagRepository;

    private final Gson gson = new Gson();

    @Override
    public String handleRequest(TileUpload tileUpload, Context context) {
        Iterable<TileFlagData> tiles = tileFlagRepository.findByLocationWithinAndPlane(new Polygon(tileUpload.getPolygon()), tileUpload.getZ());

        String json = gson.toJson(tileUpload);
        return "Sec: ";
    }
}
