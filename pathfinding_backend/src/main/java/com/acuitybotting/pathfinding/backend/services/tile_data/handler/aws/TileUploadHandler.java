package com.acuitybotting.pathfinding.backend.services.tile_data.handler.aws;

import com.acuitybotting.db.arango.entities.TileFlagData;
import com.acuitybotting.db.arango.repositories.SceneEntityRepository;
import com.acuitybotting.pathfinding.backend.services.tile_data.domain.TileUpload;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.arangodb.springframework.core.ArangoOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import java.util.Collection;
import java.util.HashSet;


/**
 * Created by Zachary Herridge on 5/29/2018.
 */
@ComponentScan("com.acuitybotting.db.arango.repositories")
public class TileUploadHandler implements RequestHandler<TileUpload, String> {

    @Autowired
    private ArangoOperations arangoOperations;

    @Override
    public String handleRequest(TileUpload tileUpload, Context context) {
        int[][] map = tileUpload.getFlags();
        Collection<TileFlagData> data = new HashSet<>();
        for (int y = 0; y < map.length; y++) {
            int[] flags = map[y];
            for (int x = 0; x < flags.length; x++) {
                int flag = flags[x];

                int worldY = tileUpload.getY() + y;
                int worldX = tileUpload.getX() + x;
                int plane = tileUpload.getPlane();


                TileFlagData build = TileFlagData.builder()
                        .plane(plane)
                        .location(new int[]{worldX, worldY})
                        .key(worldX + "_" + worldY + "_" + plane)
                        .flag(flag)
                        .requestID(context.getAwsRequestId())
                        .build();
                data.add(build);
            }
        }
        arangoOperations.upsert(data, ArangoOperations.UpsertStrategy.REPLACE);

        tileUpload.getEntities().forEach(sceneEntity -> sceneEntity.setKey(sceneEntity.getLocation()[0] + "_" + sceneEntity.getLocation()[1] + "_" + sceneEntity.getPlane() + "_" + sceneEntity.getEntityID()));
        arangoOperations.upsert(tileUpload.getEntities(), ArangoOperations.UpsertStrategy.REPLACE);
        return "Success";
    }
}
