package com.acuitybotting.pathfinding.backend.services.tile_data.handler.aws;

import com.acuitybotting.pathfinding.backend.services.tile_data.domain.RegionData;
import com.acuitybotting.pathfinding.backend.services.tile_data.utils.Constants;
import com.acuitybotting.pathfinding.backend.services.tile_data.utils.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * Created by Zachary Herridge on 5/29/2018.
 */
public class RegionUploadRequest implements RequestHandler<RegionData, String> {

    @Override
    public String handleRequest(RegionData regionData, Context context) {
        String regionName = regionData.getBase().getRegionBase().toKey();
        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        if (!s3.doesObjectExist(Constants.BUCKET_NAME, regionName)) return "true";
        return "false";
    }
}
