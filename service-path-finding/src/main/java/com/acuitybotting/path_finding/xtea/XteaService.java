package com.acuitybotting.path_finding.xtea;

import com.acuitybotting.data.flow.messaging.services.client.MessagingClientService;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionInfo;
import com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition;
import com.acuitybotting.db.arango.path_finding.domain.xtea.Xtea;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.RegionInfoRepository;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.SceneEntityDefinitionRepository;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.XteaRepository;
import com.acuitybotting.path_finding.xtea.domain.Region;
import com.acuitybotting.path_finding.xtea.domain.SceneEntityInstance;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
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

    public XteaService setInfoBase(File infoBase) {
        this.infoBase = infoBase;
        return this;
    }

    public Map<String, Set<Xtea>> findUnique(int rev) {
        return xteaRepository.findAllByRevision(rev).stream().collect(Collectors.groupingBy(object -> String.valueOf(object.getRegion()), Collectors.toSet()));
    }

    private Map<Integer, SceneEntityDefinition> cache = new HashMap<>();
    public Optional<SceneEntityDefinition> getSceneEntityDefinition(int id) {
        return Optional.ofNullable(cache.computeIfAbsent(id, integer -> definitionRepository.findById(String.valueOf(id)).orElse(null)));
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
        regionInfo.init();

        int[][][] map = new int[4][104][104];
        CollisionBuilder.padMap(map);

        int[][][] doors = new int[4][104][104];
        int[][][] renderFlags = region.getTileSettings();

        for (SceneEntityInstance entityInstance : region.getLocations()) {
            int type = entityInstance.getType();
            if (type >= 4 && type <= 8) {
                continue;
            }

            SceneEntityDefinition definition = getSceneEntityDefinition(entityInstance.getId()).orElse(null);
            if (definition == null) continue;

            int localX = entityInstance.getPosition().getX() - region.getBaseX();
            int localY = entityInstance.getPosition().getY() - region.getBaseY();
            int plane = entityInstance.getPosition().getZ();

            int hash = (localY << 7) + localX + (entityInstance.getId() << 14) + 0x4000_0000;
            if (definition.getClipType() == 0) {
                hash -= Integer.MIN_VALUE;
            }

            if (hash > 0) {
                doors[plane][localX][localY] = entityInstance.getOrientation() + 1;
            }

            if (plane < 4) {
                if (renderFlags != null && (renderFlags[1][localX][localY] & 2) == 2) {
                    plane--;
                }

                if (plane >= 0) {
                    if (type >= 0 && type <= 3) {
                        if (definition.getClipType() != 0) {
                            CollisionBuilder.method16860(map, plane, localX, localY, type, entityInstance.getOrientation(), !definition.isSolid(), !definition.isImpenetrable());
                        }
                        continue;
                    }
                    if (type == 22) {
                        if (definition.getClipType() == 1) {
                            CollisionBuilder.method16851(map, plane, localX, localY);
                        }
                    } else if (type >= 9) {
                        if (definition.getClipType() != 0) {
                            int direction = entityInstance.getOrientation();
                            if (direction != 1 && direction != 3) {
                                CollisionBuilder.method16856(map, plane, localX, localY, definition.getSizeX(), definition.getSizeY(), !definition.isSolid(), !definition.isImpenetrable());
                            } else {
                                CollisionBuilder.method16856(map, plane, localX, localY, definition.getSizeY(), definition.getSizeX(), !definition.isSolid(), !definition.isImpenetrable());
                            }
                        }
                    }
                }

            }
        }

        if (renderFlags != null) {
            CollisionBuilder.method16862(renderFlags, map);
        }

        regionInfo.setRenderSettings(renderFlags);
        regionInfo.setFlags(map);
        regionInfo.setDoors(doors);

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
