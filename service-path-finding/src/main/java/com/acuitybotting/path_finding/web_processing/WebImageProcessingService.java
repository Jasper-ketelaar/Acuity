package com.acuitybotting.path_finding.web_processing;

import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionInfo;
import com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition;
import com.acuitybotting.db.arango.path_finding.repositories.SceneEntityRepository;
import com.acuitybotting.db.arango.path_finding.repositories.TileFlagRepository;
import com.acuitybotting.path_finding.xtea.XteaService;
import com.acuitybotting.path_finding.xtea.domain.Region;
import com.acuitybotting.path_finding.xtea.domain.SceneEntityInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

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

    private static void thinking(int locationType, int clipType) {

    }

    public BufferedImage createTileFlagImage(int plane, RegionInfo regionInfo) {
        int tilePixelSize = 4;
        BufferedImage mapImage = new BufferedImage(64 * tilePixelSize, 64 * tilePixelSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D mapImageGraphics = mapImage.createGraphics();
        AffineTransform original = transform(mapImageGraphics, mapImage.getHeight());

        mapImageGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        mapImageGraphics.fillRect(0, 0, 64 * tilePixelSize, 64 * tilePixelSize);
        mapImageGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        mapImageGraphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                int localX = (x) * tilePixelSize;
                int localY = (y) * tilePixelSize;

                Integer flagAt = regionInfo.getFlags()[plane][x][y];
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

    public BufferedImage[] createTileFlagImage2(String regionId) {
        Region region = xteaService.getRegion(Integer.parseInt(regionId)).orElse(null);
        if (region == null) return null;

        int tilePixelSize = 4;

        BufferedImage[] mapImages = new BufferedImage[Region.Z];
        for (int i = 0; i < mapImages.length; i++) {
            mapImages[i] = new BufferedImage(Region.X * tilePixelSize, Region.Y * tilePixelSize, BufferedImage.TYPE_INT_RGB);
        }

        int baseX = region.getBaseX();
        int baseY = region.getBaseY();

        for (int plane = 0; plane < Region.Z; plane++) {
            for (int regionX = 0; regionX < Region.X; regionX++) {
                for (int regionY = 0; regionY < Region.Y; regionY++) {
                    int setting = region.getTileSettings()[plane][regionX][regionY];

                    int drawX = regionX * tilePixelSize;
                    int drawY = (Region.Y - 1 - regionY) * tilePixelSize;

                    boolean bridge = plane + 1 < 4 && (region.getTileSettings()[plane + 1][regionX][regionY] == 2);
                    if (!bridge && setting == 1) {
                        //blocked
                        fillTile(mapImages[plane], drawX, drawY, tilePixelSize, new Color(50, 109, 255, 223));
                    }
                    else {
                        //walkable
                        fillTile(mapImages[plane], drawX, drawY, tilePixelSize, new Color(215, 216, 216, 255));
                    }
                }
            }
        }

        for (SceneEntityInstance location : region.getLocations()) {
            int locationType = location.getType();

            int regionX = location.getPosition().getX() - baseX;
            int regionY = location.getPosition().getY() - baseY;
            int plane = location.getPosition().getZ();

            int drawX = regionX * tilePixelSize;
            int drawY = (Region.Y - 1 - regionY) * tilePixelSize;

            if (plane < 4) {
                if ((region.getTileSettings()[1][regionX][regionY] & 2) == 2) {//Settings that apply locations to plane below.
                    plane = plane - 1;
                }

                if (plane >= 0) {
                    SceneEntityDefinition baseDefinition = xteaService.getSceneEntityDefinition(location.getId()).orElseThrow(() -> new RuntimeException("Failed to load " + location.getId() + "."));

                    Integer clipType = baseDefinition.getClipType();
                    if (locationType == 22) {
                        if (clipType == 1) {
                            //block22 Never actually happens but client checks it.
                            fillTile(mapImages[plane], drawX, drawY, tilePixelSize, new Color(20, 189, 153, 198));
                        }
                    } else {
                        if (locationType != 10 && locationType != 11) {
                            int rgb = new Color(249, 122, 39, 223).getRGB();
                            int rotation = location.getOrientation();

                            if (locationType >= 12) {
                                if (clipType != 0) {
                                    //addObject block, these are visible roofs from other rooms.
                                    fillTile(mapImages[plane], drawX, drawY, tilePixelSize, new Color(189, 30, 139, 198));
                                }
                            }

                            if (baseDefinition.getItemSupport() == 1) {
                                if (locationType == 0 || locationType == 2) {
                                    if (rotation == 0) {
                                        //West wall
                                        mapImages[plane].setRGB(drawX + 0, drawY + 0, rgb);
                                        mapImages[plane].setRGB(drawX + 0, drawY + 1, rgb);
                                        mapImages[plane].setRGB(drawX + 0, drawY + 2, rgb);
                                        mapImages[plane].setRGB(drawX + 0, drawY + 3, rgb);
                                    } else if (rotation == 1) {
                                        //North wall
                                        mapImages[plane].setRGB(drawX + 0, drawY + 0, rgb);
                                        mapImages[plane].setRGB(drawX + 1, drawY + 0, rgb);
                                        mapImages[plane].setRGB(drawX + 2, drawY + 0, rgb);
                                        mapImages[plane].setRGB(drawX + 3, drawY + 0, rgb);
                                    } else if (rotation == 2) {
                                        //East wall
                                        mapImages[plane].setRGB(drawX + 3, drawY + 0, rgb);
                                        mapImages[plane].setRGB(drawX + 3, drawY + 1, rgb);
                                        mapImages[plane].setRGB(drawX + 3, drawY + 2, rgb);
                                        mapImages[plane].setRGB(drawX + 3, drawY + 3, rgb);
                                    } else if (rotation == 3) {
                                        //South wall
                                        mapImages[plane].setRGB(drawX + 0, drawY + 3, rgb);
                                        mapImages[plane].setRGB(drawX + 1, drawY + 3, rgb);
                                        mapImages[plane].setRGB(drawX + 2, drawY + 3, rgb);
                                        mapImages[plane].setRGB(drawX + 3, drawY + 3, rgb);
                                    }
                                }

                                if (locationType == 1) {
                                    //Wall interconnecting ignore
                                }

                                if (locationType == 2) {
                                    if (rotation == 3) {
                                        //West wall
                                        mapImages[plane].setRGB(drawX + 0, drawY + 0, rgb);
                                        mapImages[plane].setRGB(drawX + 0, drawY + 1, rgb);
                                        mapImages[plane].setRGB(drawX + 0, drawY + 2, rgb);
                                        mapImages[plane].setRGB(drawX + 0, drawY + 3, rgb);
                                    } else if (rotation == 0) {
                                        //North wall
                                        mapImages[plane].setRGB(drawX + 0, drawY + 0, rgb);
                                        mapImages[plane].setRGB(drawX + 1, drawY + 0, rgb);
                                        mapImages[plane].setRGB(drawX + 2, drawY + 0, rgb);
                                        mapImages[plane].setRGB(drawX + 3, drawY + 0, rgb);
                                    } else if (rotation == 1) {
                                        //East wall
                                        mapImages[plane].setRGB(drawX + 3, drawY + 0, rgb);
                                        mapImages[plane].setRGB(drawX + 3, drawY + 1, rgb);
                                        mapImages[plane].setRGB(drawX + 3, drawY + 2, rgb);
                                        mapImages[plane].setRGB(drawX + 3, drawY + 3, rgb);
                                    } else if (rotation == 2) {
                                        //South wall
                                        mapImages[plane].setRGB(drawX + 0, drawY + 3, rgb);
                                        mapImages[plane].setRGB(drawX + 1, drawY + 3, rgb);
                                        mapImages[plane].setRGB(drawX + 2, drawY + 3, rgb);
                                        mapImages[plane].setRGB(drawX + 3, drawY + 3, rgb);
                                    }
                                }

                                if (locationType == 3) {
                                    if (rotation == 0) {
                                        //Pillar North-West
                                        mapImages[plane].setRGB(drawX + 0, drawY + 0, rgb);
                                    } else if (rotation == 1) {
                                        //Pillar North-East
                                        mapImages[plane].setRGB(drawX + 3, drawY + 0, rgb);
                                    } else if (rotation == 2) {
                                        //Pillar South-East
                                        mapImages[plane].setRGB(drawX + 3, drawY + 3, rgb);
                                    } else if (rotation == 3) {
                                        //Pillar South-West
                                        mapImages[plane].setRGB(drawX + 0, drawY + 3, rgb);
                                    }
                                }

                                if (locationType == 9) {
                                    int hash = (regionX << 7) + regionY + (location.getId() << 14) + 0x4000_0000;
                                    if ((hash >> 29 & 3) != 2) {
                                        continue; //Idk works
                                    }

                                    if (rotation != 0 && rotation != 2) {
                                        //North-West to South-East wall
                                        mapImages[plane].setRGB(drawX + 0, drawY + 0, rgb);
                                        mapImages[plane].setRGB(drawX + 1, drawY + 1, rgb);
                                        mapImages[plane].setRGB(drawX + 2, drawY + 2, rgb);
                                        mapImages[plane].setRGB(drawX + 3, drawY + 3, rgb);
                                    } else {
                                        //North-East to South-West wall
                                        mapImages[plane].setRGB(drawX + 0, drawY + 3, rgb);
                                        mapImages[plane].setRGB(drawX + 1, drawY + 2, rgb);
                                        mapImages[plane].setRGB(drawX + 2, drawY + 1, rgb);
                                        mapImages[plane].setRGB(drawX + 3, drawY + 0, rgb);
                                    }
                                }
                            }

                            if (locationType == 4) {
                                //addBoundaryDecoration ignore
                            } else {
                                if (locationType == 5) {
                                    //addBoundaryDecoration ignore
                                } else if (locationType == 6) {
                                    //addBoundaryDecoration ignore
                                } else if (locationType == 7) {
                                    //addBoundaryDecoration ignore
                                } else if (locationType == 8) {
                                    //addBoundaryDecoration ignore
                                }
                            }
                        }
                        else {
                            if (!"null".equals(baseDefinition.getName()) && baseDefinition.getProjectileClipped() && baseDefinition.getItemSupport() == 1) {
                                //addObject blocks walking

                                int width;
                                int length;

                                int orientation = location.getOrientation();
                                if(orientation != 1 && orientation != 3) {
                                    width = baseDefinition.getSizeX();
                                    length = baseDefinition.getSizeY();
                                }
                                else {
                                    width = baseDefinition.getSizeY();
                                    length = baseDefinition.getSizeX();
                                }

                                for (int xOff = 0; xOff < width; xOff++) {
                                    for (int yOff = 0; yOff < length; yOff++) {
                                        drawX = (regionX + xOff) * tilePixelSize;
                                        drawY = (Region.Y - 1 - (regionY + yOff)) * tilePixelSize;
                                        fillTile(mapImages[plane], drawX, drawY, tilePixelSize, new Color(51, 189, 20, 198));
                                    }
                                }
                            }
                        }
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
