package com.acuitybotting.pathfinding.backend.services.tile_data.handler.aws;

import com.acuitybotting.db.arango.repositories.TileFlagRepository;
import com.acuitybotting.pathfinding.backend.services.tile_data.domain.TileCaptureCheck;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;

import java.util.Arrays;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
@ComponentScan("com.acuitybotting.db.arango.repositories")
public class RegionUploadRequest implements RequestHandler<TileCaptureCheck, String> {

    @Autowired
    private TileFlagRepository repository;


    @Override
    public String handleRequest(TileCaptureCheck tileCaptureCheck, Context context) {

        long flagsFound = repository.countByLocationWithinAndPlane(new Polygon(Arrays.asList(
                new Point(tileCaptureCheck.getX(), tileCaptureCheck.getY()),
                new Point(tileCaptureCheck.getX() + tileCaptureCheck.getWidth(), tileCaptureCheck.getY()),
                new Point(tileCaptureCheck.getX() + tileCaptureCheck.getWidth(), tileCaptureCheck.getY() + tileCaptureCheck.getHeight()),
                new Point(tileCaptureCheck.getX(), tileCaptureCheck.getY() + tileCaptureCheck.getHeight())
                )),
                tileCaptureCheck.getZ()
        );

        return String.valueOf(flagsFound < tileCaptureCheck.getHeight() * tileCaptureCheck.getWidth());
    }
}
