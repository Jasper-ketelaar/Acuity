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
        long tilesFound = repository.countAllByXBetweenAndYBetweenAndPlaneAndDeprecatedNot(
                tileCaptureCheck.getX(),
                tileCaptureCheck.getX() + tileCaptureCheck.getWidth(),
                tileCaptureCheck.getY(),
                tileCaptureCheck.getY() + tileCaptureCheck.getWidth(),
                tileCaptureCheck.getPlane()
        );

        int capturedTiles = tileCaptureCheck.getHeight() * tileCaptureCheck.getWidth();
        return tilesFound - capturedTiles;
    }

    public boolean save(TileCapture tileCapture) {
        int[][] collisionData = tileCapture.getFlags();
        if (collisionData != null){
            Collection<TileFlag> data = new HashSet<>();

            for (int i = 1; i < collisionData.length - 5; i++) {
                for (int j = 1; j < collisionData[i].length - 5; j++) {
                    int worldX = tileCapture.getX() + i;
                    int worldY = tileCapture.getY() + j;
                    int plane = tileCapture.getPlane();
                    int flag = collisionData[i][j];

                    TileFlag tileFlag = new TileFlag();
                    tileFlag.setX(worldX);
                    tileFlag.setY(worldY);
                    tileFlag.setPlane(plane);
                    tileFlag.setFlag(flag);
                    tileFlag.setKey(worldX + "_" + worldY + "_" + plane);

                    data.add(tileFlag);
                }
            }
            arangoOperations.upsert(data, ArangoOperations.UpsertStrategy.REPLACE);
        }

        if (tileCapture.getEntities() != null){
            tileCapture.getEntities().forEach(sceneEntity -> sceneEntity.setKey(sceneEntity.getX() + "_" + sceneEntity.getY() + "_" + sceneEntity.getPlane() + "_" + sceneEntity.getEntityID()));
            arangoOperations.upsert(tileCapture.getEntities(), ArangoOperations.UpsertStrategy.REPLACE);
        }

        return true;
    }
}
