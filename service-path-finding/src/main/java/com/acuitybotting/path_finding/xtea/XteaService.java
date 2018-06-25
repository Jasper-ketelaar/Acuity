package com.acuitybotting.path_finding.xtea;

import com.acuitybotting.data.flow.messaging.services.client.MessagingClientService;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionInfo;
import com.acuitybotting.db.arango.path_finding.domain.xtea.Xtea;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.RegionInfoRepository;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.SceneEntityDefinitionRepository;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.XteaRepository;
import com.acuitybotting.path_finding.xtea.domain.Region;
import com.acuitybotting.path_finding.xtea.domain.SceneEntity;
import com.acuitybotting.path_finding.xtea.domain.SceneEntityDefinition;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 6/22/2018.
 */
@Service
@Getter
@Slf4j
public class XteaService {

    private static final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/604080725100/acuitybotting-xtea-dump.fifo";

    private final SceneEntityDefinitionRepository definitionRepository;
    private final XteaRepository xteaRepository;

    private final RegionInfoRepository regionInfoRepository;
    private final MessagingClientService clientService;
    private Gson gson = new Gson();

    private File infoBase;

    @Autowired
    public XteaService(SceneEntityDefinitionRepository definitionRepository, XteaRepository xteaRepository, RegionInfoRepository regionInfoRepository, MessagingClientService clientService) {
        this.definitionRepository = definitionRepository;
        this.xteaRepository = xteaRepository;
        this.regionInfoRepository = regionInfoRepository;
        this.clientService = clientService;
    }

    public int worldToRegionId(int worldX, int worldY) {
        worldX >>>= 6;
        worldY >>>= 6;
        return (worldX << 8) | worldY;
    }

    public XteaService setInfoBase(File infoBase) {
        this.infoBase = infoBase;
        return this;
    }

    public Map<String, Set<Xtea>> findUnique(int rev) {
        return xteaRepository.findAllByRevision(rev).stream().collect(Collectors.groupingBy(object -> String.valueOf(object.getRegion()), Collectors.toSet()));
    }

    public Optional<SceneEntityDefinition> getSceneEntityDefinition(int id) {
        try {
            return Optional.of(gson.fromJson(new FileReader(new File(infoBase, "\\json\\objects\\" + id + ".json")), SceneEntityDefinition.class));
        } catch (FileNotFoundException e) {
            log.warn("Error loading object info.", e);
        }
        return Optional.empty();
    }

    public Optional<Region> getRegion(int id) {
        try {
            return Optional.of(gson.fromJson(new FileReader(new File(infoBase, "\\json\\regions\\" + id + ".json")), Region.class));
        } catch (FileNotFoundException e) {
            log.warn("Error loading region info.", e);
        }
        return Optional.empty();
    }

    public RegionInfo save(Region region) {
        RegionInfo regionInfo = new RegionInfo();
        regionInfo.setKey(String.valueOf(region.getRegionID()));

        int[][][] map = new int[4][104][104];
        byte[][][] renderFlags = region.getTileSettings();

        for (SceneEntity sceneEntity : region.getLocations()) {
            SceneEntityDefinition definition = getSceneEntityDefinition(sceneEntity.getId()).orElse(null);
            if (definition == null) continue;

            int type = sceneEntity.getType();
            int localX = sceneEntity.getPosition().getX() - region.getBaseX();
            int localY = sceneEntity.getPosition().getY() - region.getBaseY();
            int plane = sceneEntity.getPosition().getZ();

            if (type >= 0 && type <= 3) {
                int hash = (localY << 7) + localX + (sceneEntity.getId() << 14) + 0x4000_0000;
                if (definition.getAnInt2088() == 0) {
                    hash -= Integer.MIN_VALUE;
                }

                if (plane < 4) {
                    if (renderFlags != null && (renderFlags[1][localX][localY] & 2) == 2) {
                        plane--;
                    }

                    if (plane >= 0) {
                        if (type >= 0 && type <= 3) {
                            if (definition.getAnInt2088() != 0) {
                                CollisionBuilder.method16860(map, plane, localX, localY, type, sceneEntity.getOrientation(), !definition.isSolid(), !definition.isBlocksProjectile());
                            }
                            continue;
                        }
                        if (type == 22) {
                            if (definition.getAnInt2088() == 1) {
                                CollisionBuilder.method16851(map, plane, localX, localY);
                            }
                        } else if (type >= 9) {
                            if (definition.getAnInt2088() != 0) {
                                int direction = sceneEntity.getOrientation();
                                if (direction != 1 && direction != 3) {
                                    CollisionBuilder.method16856(map, plane, localX, localY, definition.getModelSizeX(), definition.getModelSizeY(), !definition.isSolid(), !definition.isBlocksProjectile());
                                } else {
                                    CollisionBuilder.method16856(map, plane, localX, localY, definition.getModelSizeY(), definition.getModelSizeX(), !definition.isSolid(), !definition.isBlocksProjectile());
                                }
                            }
                        }
                    }
                }
            }
        }

        regionInfo.setRenderSettings(renderFlags);
        regionInfo.setFlags(map);

        return regionInfoRepository.save(regionInfo);
    }

    public void consumeQueue() {
        int[] emptyKeys = {0, 0, 0, 0};

        clientService.setDeleteMessageOnConsume(false).consumeQueue(QUEUE_URL, message -> {
            try {
                Xtea[] xteas = gson.fromJson(message.getBody(), Xtea[].class);
                for (Xtea xtea : xteas) {
                    if (xtea.getKeys() == null || Arrays.equals(xtea.getKeys(), emptyKeys)) continue;
                    xteaRepository.save(xtea);
                    log.info("Saved Xtea Key {}.", xtea);
                }
                clientService.deleteMessage(QUEUE_URL, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
