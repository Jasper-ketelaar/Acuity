package com.acuitybotting.path_finding.tile_data.api;

import com.acuitybotting.db.arango.repositories.TileFlagRepository;
import com.acuitybotting.path_finding.tile_data.domain.TileCaptureCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Function;

@Component("tileUploadCheckFunction")
public class TileUploadCheckFunction implements Function<TileCaptureCheck, String> {

    @Autowired
    private TileFlagRepository repository;

    @Override
    public String apply(TileCaptureCheck tileCaptureCheck) {
        long tilesFound = repository.countByLocationWithinAndPlane(new Polygon(Arrays.asList(
                new Point(tileCaptureCheck.getX(), tileCaptureCheck.getY()),
                new Point(tileCaptureCheck.getX() + tileCaptureCheck.getWidth(), tileCaptureCheck.getY()),
                new Point(tileCaptureCheck.getX() + tileCaptureCheck.getWidth(), tileCaptureCheck.getY() + tileCaptureCheck.getHeight()),
                new Point(tileCaptureCheck.getX(), tileCaptureCheck.getY() + tileCaptureCheck.getHeight())
                )),
                tileCaptureCheck.getPlane()
        );

        int capturedTiles = tileCaptureCheck.getHeight() * tileCaptureCheck.getWidth();
        return tileCaptureCheck.toString() + " : " + String.valueOf(tilesFound < capturedTiles);
    }
}