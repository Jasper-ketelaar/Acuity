package com.acuitybotting.path_finding.web_processing;

import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionMap;
import com.acuitybotting.path_finding.rs.utils.MapFlags;
import com.acuitybotting.path_finding.xtea.XteaService;
import com.acuitybotting.path_finding.xtea.domain.RsRegion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Zachary Herridge on 6/5/2018.
 */
@Service
public class WebImageProcessingService {

    private XteaService xteaService;

    @Autowired
    public WebImageProcessingService(XteaService xteaService) {
        this.xteaService = xteaService;
    }

    public BufferedImage[] createTileFlagImageFromRegionInfo(RegionMap regionMap) {
        int tilePixelSize = 4;

        BufferedImage[] mapImages = new BufferedImage[RsRegion.Z];
        for (int i = 0; i < mapImages.length; i++) {
            mapImages[i] = new BufferedImage(RsRegion.X * tilePixelSize, RsRegion.Y * tilePixelSize, BufferedImage.TYPE_INT_RGB);
        }

        for (int plane = 0; plane < RsRegion.Z; plane++) {
            for (int regionX = 0; regionX < RsRegion.X; regionX++) {
                for (int regionY = 0; regionY < RsRegion.Y; regionY++) {
                    int drawX = regionX * tilePixelSize;
                    int drawY = (RsRegion.Y - 1 - regionY) * tilePixelSize;
                    if(regionMap.checkFlag(regionX, regionY, plane, MapFlags.BLOCKED_SETTING)){
                        fillTile(mapImages[plane], drawX, drawY, tilePixelSize, new Color(50, 109, 255, 223));
                    }
                    else {
                        fillTile(mapImages[plane], drawX, drawY, tilePixelSize, new Color(215, 216, 216, 255));
                    }
                }
            }
        }

        int rgb = new Color(249, 122, 39, 223).getRGB();

        for (int plane = 0; plane < RsRegion.Z; plane++) {
            for (int regionX = 0; regionX < RsRegion.X; regionX++) {
                for (int regionY = 0; regionY < RsRegion.Y; regionY++) {
                    int drawX = regionX * tilePixelSize;
                    int drawY = (RsRegion.Y - 1 - regionY) * tilePixelSize;

                    if(regionMap.checkFlag(regionX, regionY, plane, MapFlags.WALL_WEST)){
                        mapImages[plane].setRGB(drawX + 0, drawY + 0, rgb);
                        mapImages[plane].setRGB(drawX + 0, drawY + 1, rgb);
                        mapImages[plane].setRGB(drawX + 0, drawY + 2, rgb);
                        mapImages[plane].setRGB(drawX + 0, drawY + 3, rgb);
                    }

                    if(regionMap.checkFlag(regionX, regionY, plane, MapFlags.WALL_NORTH)){
                        mapImages[plane].setRGB(drawX + 0, drawY + 0, rgb);
                        mapImages[plane].setRGB(drawX + 1, drawY + 0, rgb);
                        mapImages[plane].setRGB(drawX + 2, drawY + 0, rgb);
                        mapImages[plane].setRGB(drawX + 3, drawY + 0, rgb);
                    }

                    if(regionMap.checkFlag(regionX, regionY, plane, MapFlags.WALL_EAST)){
                        mapImages[plane].setRGB(drawX + 3, drawY + 0, rgb);
                        mapImages[plane].setRGB(drawX + 3, drawY + 1, rgb);
                        mapImages[plane].setRGB(drawX + 3, drawY + 2, rgb);
                        mapImages[plane].setRGB(drawX + 3, drawY + 3, rgb);
                    }

                    if(regionMap.checkFlag(regionX, regionY, plane, MapFlags.WALL_SOUTH)){
                        mapImages[plane].setRGB(drawX + 0, drawY + 3, rgb);
                        mapImages[plane].setRGB(drawX + 1, drawY + 3, rgb);
                        mapImages[plane].setRGB(drawX + 2, drawY + 3, rgb);
                        mapImages[plane].setRGB(drawX + 3, drawY + 3, rgb);
                    }

                    if(regionMap.checkFlag(regionX, regionY, plane, MapFlags.PILLAR_NORTH_WEST)){
                        mapImages[plane].setRGB(drawX + 0, drawY + 0, rgb);
                    }

                    if(regionMap.checkFlag(regionX, regionY, plane, MapFlags.PILLAR_NORTH_EAST)){
                        mapImages[plane].setRGB(drawX + 3, drawY + 0, rgb);
                    }

                    if(regionMap.checkFlag(regionX, regionY, plane, MapFlags.PILLAR_SOUTH_EAST)){
                        mapImages[plane].setRGB(drawX + 3, drawY + 3, rgb);
                    }

                    if(regionMap.checkFlag(regionX, regionY, plane, MapFlags.PILLAR_SOUTH_WEST)){
                        mapImages[plane].setRGB(drawX + 0, drawY + 3, rgb);
                    }

                    if(regionMap.checkFlag(regionX, regionY, plane, MapFlags.WALL_NORTH_WEST_TO_SOUTH_EAST)){
                        mapImages[plane].setRGB(drawX + 0, drawY + 0, rgb);
                        mapImages[plane].setRGB(drawX + 1, drawY + 1, rgb);
                        mapImages[plane].setRGB(drawX + 2, drawY + 2, rgb);
                        mapImages[plane].setRGB(drawX + 3, drawY + 3, rgb);
                    }

                    if(regionMap.checkFlag(regionX, regionY, plane, MapFlags.WALL_NORTH_EAST_TO_SOUTH_WEST)){
                        mapImages[plane].setRGB(drawX + 0, drawY + 3, rgb);
                        mapImages[plane].setRGB(drawX + 1, drawY + 2, rgb);
                        mapImages[plane].setRGB(drawX + 2, drawY + 1, rgb);
                        mapImages[plane].setRGB(drawX + 3, drawY + 0, rgb);
                    }

                    if(regionMap.checkFlag(regionX, regionY, plane, MapFlags.BLOCKED_ROOF)){
                        fillTile(mapImages[plane], drawX, drawY, tilePixelSize, new Color(189, 30, 139, 198));
                    }

                    if(regionMap.checkFlag(regionX, regionY, plane, MapFlags.BLOCKED_SCENE_OBJECT)){
                        fillTile(mapImages[plane], drawX, drawY, tilePixelSize, new Color(51, 189, 20, 198));
                    }
                }
            }
        }

        return mapImages;
    }

    private void fillTile(BufferedImage image, int drawX, int drawY, int tilePixelSize, Color color) {
        int rgb = color.getRGB();
        for (int x = 0; x < tilePixelSize; x++) {
            for (int y = 0; y < tilePixelSize; y++) {
                image.setRGB(drawX + x, drawY + y, rgb);
            }
        }
    }
}
