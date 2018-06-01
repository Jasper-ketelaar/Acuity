package com.acuitybotting.path_finding.tile_capture.service;

import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.acuitybotting.db.arango.path_finding.repositories.TileFlagRepository;
import com.acuitybotting.path_finding.tile_capture.domain.TileCapture;
import com.acuitybotting.path_finding.tile_capture.domain.TileCaptureCheck;
import com.arangodb.springframework.core.ArangoOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Service
public class TileCaptureService {

    private final TileFlagRepository repository;

    private final ArangoOperations arangoOperations;

    @Autowired
    public TileCaptureService(TileFlagRepository repository, ArangoOperations arangoOperations) {
        this.repository = repository;
        this.arangoOperations = arangoOperations;
    }

    public long getTileDifference(TileCaptureCheck tileCaptureCheck) {
        long tilesFound = repository.countByLocationWithinAndPlane(new Polygon(Arrays.asList(
                new Point(tileCaptureCheck.getX(), tileCaptureCheck.getY()),
                new Point(tileCaptureCheck.getX() + tileCaptureCheck.getWidth(), tileCaptureCheck.getY()),
                new Point(tileCaptureCheck.getX() + tileCaptureCheck.getWidth(), tileCaptureCheck.getY() + tileCaptureCheck.getHeight()),
                new Point(tileCaptureCheck.getX(), tileCaptureCheck.getY() + tileCaptureCheck.getHeight())
                )),
                tileCaptureCheck.getPlane()
        );

        int capturedTiles = tileCaptureCheck.getHeight() * tileCaptureCheck.getWidth();
        return tilesFound - capturedTiles;
    }

    public boolean save(TileCapture tileCapture) {
        int[][] map = tileCapture.getFlags();
        if (map != null){
            Collection<TileFlag> data = new HashSet<>();
            for (int y = 0; y < map.length; y++) {
                int[] flags = map[y];
                for (int x = 0; x < flags.length; x++) {
                    int flag = flags[x];

                    int worldY = tileCapture.getY() + y;
                    int worldX = tileCapture.getX() + x;
                    int plane = tileCapture.getPlane();

                    TileFlag build = TileFlag.builder()
                            .plane(plane)
                            .location(new int[]{worldX, worldY})
                            .key(worldX + "_" + worldY + "_" + plane)
                            .flag(flag)
                            .build();
                    data.add(build);
                }
            }
            arangoOperations.upsert(data, ArangoOperations.UpsertStrategy.REPLACE);
        }

        if (tileCapture.getEntities() != null){
            tileCapture.getEntities().forEach(sceneEntity -> sceneEntity.setKey(sceneEntity.getLocation()[0] + "_" + sceneEntity.getLocation()[1] + "_" + sceneEntity.getPlane() + "_" + sceneEntity.getEntityID()));
            arangoOperations.upsert(tileCapture.getEntities(), ArangoOperations.UpsertStrategy.REPLACE);
        }

        return true;
    }
}
