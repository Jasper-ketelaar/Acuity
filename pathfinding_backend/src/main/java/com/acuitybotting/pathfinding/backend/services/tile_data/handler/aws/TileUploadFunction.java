package com.acuitybotting.pathfinding.backend.services.tile_data.handler.aws;

import com.acuitybotting.db.arango.entities.TileFlagData;
import com.acuitybotting.pathfinding.backend.services.tile_data.domain.TileCapture;
import com.arangodb.springframework.core.ArangoOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
@Component("tileUploadFunction")
public class TileUploadFunction implements Function<TileCapture, String> {

    @Autowired
    private ArangoOperations arangoOperations;

    @Override
    public String apply(TileCapture tileCapture) {
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
