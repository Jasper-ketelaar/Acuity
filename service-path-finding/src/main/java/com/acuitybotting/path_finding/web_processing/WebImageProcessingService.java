package com.acuitybotting.path_finding.web_processing;

import com.acuitybotting.db.arango.path_finding.domain.SceneEntity;
import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionInfo;
import com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition;
import com.acuitybotting.db.arango.path_finding.repositories.SceneEntityRepository;
import com.acuitybotting.db.arango.path_finding.repositories.TileFlagRepository;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.rs.utils.RsMapService;
import com.acuitybotting.path_finding.xtea.XteaService;
import com.acuitybotting.path_finding.xtea.domain.Region;
import com.acuitybotting.path_finding.xtea.domain.SceneEntityInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 6/5/2018.
 */
@Service
public class WebImageProcessingService {

    private XteaService xteaService;
    private TileFlagRepository flagRepository;
    private SceneEntityRepository sceneEntityRepository;

    @Autowired
    public WebImageProcessingService(XteaService xteaService, TileFlagRepository flagRepository, SceneEntityRepository sceneEntityRepository) {
        this.xteaService = xteaService;
        this.flagRepository = flagRepository;
        this.sceneEntityRepository = sceneEntityRepository;
    }

    public BufferedImage createDoorImage(int plane, int baseX, int baseY, int regionWidth, int regionHeight, int tilePixelSize) {
        BufferedImage mapImage = createTileFlagImage(plane, baseX, baseY, regionWidth, regionHeight, tilePixelSize);
        Graphics2D mapImageGraphics = mapImage.createGraphics();
        AffineTransform original = transform(mapImageGraphics, mapImage.getHeight());

        mapImageGraphics.setColor(new Color(255, 145, 232, 223));
        Iterable<SceneEntity> doors = sceneEntityRepository.findAllByXBetweenAndYBetweenAndPlaneAndNameIn(baseX, baseX + regionWidth, baseY, baseY + regionHeight, plane, RsEnvironment.DOOR_NAMES);
        for (SceneEntity sceneEntity : doors) {
            int localX = (sceneEntity.getX() - baseX) * tilePixelSize;
            int localY = (sceneEntity.getY() - baseY) * tilePixelSize;
            mapImageGraphics.fillRect(localX, localY, tilePixelSize, tilePixelSize);
        }

        mapImageGraphics.setTransform(original);
        return mapImage;
    }

    public BufferedImage createTileFlagImage(int plane, RegionInfo regionInfo) {
        Location base = RsMapService.regionIdToBase(Integer.parseInt(regionInfo.getKey()));
        return createTileFlagImage(plane, base.getX(), base.getY(), 64, 64, 4);
    }

    public BufferedImage createTileFlagImage(int plane, int baseX, int baseY, int regionWidth, int regionHeight, int tilePixelSize) {
        BufferedImage mapImage = new BufferedImage(regionWidth * tilePixelSize, regionHeight * tilePixelSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D mapImageGraphics = mapImage.createGraphics();
        AffineTransform original = transform(mapImageGraphics, mapImage.getHeight());

        mapImageGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        mapImageGraphics.fillRect(0, 0, regionWidth * tilePixelSize, regionHeight * tilePixelSize);
        mapImageGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        mapImageGraphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        for (int x = baseX - 1; x < baseX + regionWidth; x++) {
            for (int y = baseY - 1; y < baseY + regionHeight; y++) {
                int localX = (x - baseX) * tilePixelSize;
                int localY = (y - baseY) * tilePixelSize;

                Integer flagAt = RsEnvironment.getFlagAt(new Location(x, y, plane));
                if (flagAt == null) continue;
                TileFlag tileFlag = new TileFlag();
                tileFlag.setFlag(flagAt);

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
        }

        mapImageGraphics.setTransform(original);
        return mapImage;
    }

    private AffineTransform transform(Graphics2D graphics2D, int height) {
        AffineTransform old = graphics2D.getTransform();
        graphics2D.translate(0, height - 1);
        graphics2D.scale(1, -1);
        return old;
    }

    public BufferedImage createTileFlagImage2(int plane, RegionInfo regionInfo) {
        Region region = xteaService.getRegion(Integer.parseInt(regionInfo.getKey())).orElse(null);
        if (region == null) return null;

        int tilePixelSize = 4;

        BufferedImage mapImage = new BufferedImage(64 * tilePixelSize, 64 * tilePixelSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D mapImageGraphics = mapImage.createGraphics();
        AffineTransform original = transform(mapImageGraphics, mapImage.getHeight());

        int baseX = region.getBaseX();
        int baseY = region.getBaseY();

        for (int regionX = 0; regionX < 64; regionX++) {
            for (int regionY = 0; regionY < 64; regionY++) {
                int setting = region.getTileSettings()[plane][regionX][regionY];

                int pixelX = regionX * tilePixelSize;
                int pixelY = regionY * tilePixelSize;

                if (setting == 1){
                    mapImageGraphics.setColor(new Color(17, 47, 80, 70));
                    mapImageGraphics.fillRect(pixelX, pixelY, tilePixelSize, tilePixelSize);
                }
            }
        }

        for (SceneEntityInstance location : region.getLocations()) {
            boolean isBridge = (region.getTileSetting(location.getPosition().toLocation()) & 2) != 0;

            if (location.getPosition().getZ() == plane + 1) {
                if (!isBridge) {
                    continue;
                }
            } else if (location.getPosition().getZ() == plane) {
                if (isBridge) {
                    continue;
                }

                if ((region.getTileSetting(location.getPosition().toLocation()) & 24) != 0) {
                    continue;
                }
            } else {
                continue;
            }

            int pixelX = (location.getPosition().getX() - baseX) * tilePixelSize;
            int pixelY = (location.getPosition().getY() - baseY) * tilePixelSize;

            if (location.getType() >= 0 && location.getType() <= 3) {
                mapImageGraphics.setColor(new Color(249, 193, 58, 159));
                mapImageGraphics.fillRect(pixelX, pixelY, tilePixelSize, tilePixelSize);
            }

            if (location.getType() == 22 || (location.getType() >= 9 && location.getType() <= 11)){
                SceneEntityDefinition sceneEntityDefinition = xteaService.getSceneEntityDefinition(location.getId()).orElseThrow(() -> new RuntimeException("Failed to load def for " + location.getId() + ".'"));
                if (sceneEntityDefinition.getSolid()){
                    mapImageGraphics.setColor(new Color(27, 249, 27, 159));
                    mapImageGraphics.fillOval(pixelX, pixelY, tilePixelSize, tilePixelSize);
                }
                if (sceneEntityDefinition.getImpenetrable()){
                    mapImageGraphics.setColor(new Color(249, 107, 240, 50));
                    mapImageGraphics.fillRect(pixelX, pixelY, tilePixelSize, tilePixelSize);
                }
            }

      /*      if (location.getType() >= 4 && location.getType() <= 8) {
                mapImageGraphics.setColor(new Color(44, 233, 33, 70));
                mapImageGraphics.fillRect(pixelX, pixelY, tilePixelSize, tilePixelSize);
            }

            if (location.getType() == 9) {
                mapImageGraphics.setColor(new Color(100, 160, 244, 70));
                mapImageGraphics.fillRect(pixelX, pixelY, tilePixelSize, tilePixelSize);
            }*/
        }

        mapImageGraphics.setTransform(original);
        return mapImage;
    }
}
