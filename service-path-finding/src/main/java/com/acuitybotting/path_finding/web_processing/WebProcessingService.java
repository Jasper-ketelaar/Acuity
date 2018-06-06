package com.acuitybotting.path_finding.web_processing;

import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.acuitybotting.db.arango.path_finding.repositories.TileFlagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Zachary Herridge on 6/5/2018.
 */
@Service
public class WebProcessingService {

    private TileFlagRepository flagRepository;

    @Autowired
    public WebProcessingService(TileFlagRepository flagRepository) {
        this.flagRepository = flagRepository;
    }

    public BufferedImage createImage(int plane, int regionWidth, int regionHeight, int tilePixelSize){
        BufferedImage nonDisplayableMapImage = new BufferedImage(regionWidth * tilePixelSize, regionHeight * tilePixelSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D nonDisplayableMapImageGraphics = nonDisplayableMapImage.createGraphics();

        nonDisplayableMapImageGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        nonDisplayableMapImageGraphics.fillRect(0, 0, regionWidth * tilePixelSize, regionHeight * tilePixelSize);
        nonDisplayableMapImageGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        nonDisplayableMapImageGraphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        for (TileFlag realTimeCollisionTile : flagRepository.findAllByPlane(plane)) {
            int localX = realTimeCollisionTile.getX()* tilePixelSize;
            int localY = realTimeCollisionTile.getY() * tilePixelSize;

            nonDisplayableMapImageGraphics.setColor(new Color(255, 255, 255, 47));
            nonDisplayableMapImageGraphics.fillRect(localX, localY, tilePixelSize, tilePixelSize);

            if (!realTimeCollisionTile.isWalkable()) {
                nonDisplayableMapImageGraphics.setColor(new Color(255, 170, 4, 161));
                nonDisplayableMapImageGraphics.fillRect(localX, localY, tilePixelSize, tilePixelSize);
            }

            nonDisplayableMapImageGraphics.setColor(new Color(255, 254, 253, 223));
            if (realTimeCollisionTile.blockedNorth()) {
                nonDisplayableMapImageGraphics.fillRect(localX, localY, tilePixelSize, tilePixelSize / 4);
            }
            if (realTimeCollisionTile.blockedEast()) {
                nonDisplayableMapImageGraphics.fillRect(localX + tilePixelSize - tilePixelSize / 4, localY, tilePixelSize / 4, tilePixelSize);
            }
            if (realTimeCollisionTile.blockedSouth()) {
                nonDisplayableMapImageGraphics.fillRect(localX, localY + tilePixelSize - tilePixelSize / 4, tilePixelSize, tilePixelSize / 4);
            }
            if (realTimeCollisionTile.blockedWest()) {
                nonDisplayableMapImageGraphics.fillRect(localX, localY, tilePixelSize / 4, tilePixelSize);
            }
        }

        BufferedImage mapDisplay = new BufferedImage(regionWidth * tilePixelSize, regionHeight * tilePixelSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D mapGraphicsDisplay = mapDisplay.createGraphics();
        mapGraphicsDisplay.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        mapGraphicsDisplay.fillRect(0, 0, regionWidth * tilePixelSize, regionHeight * tilePixelSize);
        mapGraphicsDisplay.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        mapGraphicsDisplay.drawImage(nonDisplayableMapImage, 0, 0, null);

        return mapDisplay;
    }
}
