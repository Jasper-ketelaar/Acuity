package com.acuitybotting.path_finding.tile_data.api;

import com.acuitybotting.db.arango.domain.TileFlagData;
import com.acuitybotting.db.arango.repositories.TileFlagRepository;
import com.acuitybotting.path_finding.tile_data.domain.TileCapture;
import com.acuitybotting.path_finding.tile_data.domain.TileCaptureCheck;
import com.arangodb.springframework.core.ArangoOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

@RestController
public class TileUploadAPI {

    @Autowired
    private TileFlagRepository repository;

    @Autowired
    private ArangoOperations arangoOperations;

    @RequestMapping(value = "tileCheck", method = RequestMethod.POST)
    public String tileCheck(TileCaptureCheck tileCaptureCheck) {
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

    @RequestMapping(value = "tileUpload", method = RequestMethod.POST)
    public String tileUpload(TileCapture tileCapture) {
        int[][] map = tileCapture.getFlags();
        if (map != null){
            Collection<TileFlagData> data = new HashSet<>();
            for (int y = 0; y < map.length; y++) {
                int[] flags = map[y];
                for (int x = 0; x < flags.length; x++) {
                    int flag = flags[x];

                    int worldY = tileCapture.getY() + y;
                    int worldX = tileCapture.getX() + x;
                    int plane = tileCapture.getPlane();


                    TileFlagData build = TileFlagData.builder()
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

        return "Success";
    }
}
