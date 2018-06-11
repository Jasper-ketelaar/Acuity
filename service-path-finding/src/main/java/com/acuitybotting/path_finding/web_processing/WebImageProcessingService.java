package com.acuitybotting.path_finding.web_processing;

import com.acuitybotting.db.arango.path_finding.domain.SceneEntity;
import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.acuitybotting.db.arango.path_finding.repositories.SceneEntityRepository;
import com.acuitybotting.db.arango.path_finding.repositories.TileFlagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by Zachary Herridge on 6/5/2018.
 */
@Service
public class WebImageProcessingService {

    private TileFlagRepository flagRepository;
    private SceneEntityRepository sceneEntityRepository;

    @Autowired
    public WebImageProcessingService(TileFlagRepository flagRepository, SceneEntityRepository sceneEntityRepository) {
        this.flagRepository = flagRepository;
        this.sceneEntityRepository = sceneEntityRepository;
    }

    public BufferedImage createDoorImage(int plane, int baseX, int baseY, int regionWidth, int regionHeight, int tilePixelSize){
        BufferedImage mapImage = createTileFlagImage(plane, baseX, baseY, regionWidth, regionHeight, tilePixelSize);
        Graphics2D mapImageGraphics = mapImage.createGraphics();

        String[] doorNames = new String[]{"Door"};
        mapImageGraphics.setColor(new Color(255, 145, 232, 223));
        Iterable<SceneEntity> doors = sceneEntityRepository.findAllByXBetweenAndYBetweenAndPlaneAndNameIn(baseX, baseX + regionWidth, baseY, baseY + regionHeight, plane, doorNames);
        for (SceneEntity sceneEntity : doors) {
            int localX = (sceneEntity.getX() - baseX) * tilePixelSize;
            int localY = (sceneEntity.getY() - baseY) * tilePixelSize;
            mapImageGraphics.fillRect(localX, localY, tilePixelSize, tilePixelSize);
        }

        return mapImage;
    }

    public BufferedImage createTileFlagImage(int plane, int baseX, int baseY, int regionWidth, int regionHeight, int tilePixelSize){
        BufferedImage mapImage = new BufferedImage(regionWidth * tilePixelSize, regionHeight * tilePixelSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D mapImageGraphics = mapImage.createGraphics();

        mapImageGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        mapImageGraphics.fillRect(0, 0, regionWidth * tilePixelSize, regionHeight * tilePixelSize);
        mapImageGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        mapImageGraphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        for (TileFlag tileFlag : flagRepository.findAllByXBetweenAndYBetweenAndPlane(baseX, baseX + regionWidth, baseY, baseY + regionHeight, plane)) {
            int localX = (tileFlag.getX() - baseX)* tilePixelSize;
            int localY = (tileFlag.getY() - baseY) * tilePixelSize;

            mapImageGraphics.setColor(new Color(3, 1, 3, 47));
            mapImageGraphics.fillRect(localX, localY, tilePixelSize, tilePixelSize);

            if (!tileFlag.isWalkable()) {
                mapImageGraphics.setColor(new Color(50, 109, 255, 223));
                mapImageGraphics.fillRect(localX, localY, tilePixelSize, tilePixelSize);
            }

            mapImageGraphics.setColor(new Color(249, 122, 39, 223));
            if (tileFlag.blockedNorth()) {
                mapImageGraphics.fillRect(localX, localY, tilePixelSize, tilePixelSize / 4);
            }
            if (tileFlag.blockedEast()) {
                mapImageGraphics.fillRect(localX + tilePixelSize - tilePixelSize / 4, localY, tilePixelSize / 4, tilePixelSize);
            }
            if (tileFlag.blockedSouth()) {
                mapImageGraphics.fillRect(localX, localY + tilePixelSize - tilePixelSize / 4, tilePixelSize, tilePixelSize / 4);
            }
            if (tileFlag.blockedWest()) {
                mapImageGraphics.fillRect(localX, localY, tilePixelSize / 4, tilePixelSize);
            }
        }

        return mapImage;
    }
}
