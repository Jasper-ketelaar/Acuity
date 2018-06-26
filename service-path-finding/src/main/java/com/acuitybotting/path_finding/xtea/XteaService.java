package com.acuitybotting.path_finding.xtea;

import com.acuitybotting.data.flow.messaging.services.client.MessagingClientService;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionInfo;
import com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition;
import com.acuitybotting.db.arango.path_finding.domain.xtea.Xtea;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.RegionInfoRepository;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.SceneEntityDefinitionRepository;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.XteaRepository;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
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

    @Autowired
    public XteaService(SceneEntityDefinitionRepository definitionRepository, XteaRepository xteaRepository, RegionInfoRepository regionInfoRepository, MessagingClientService clientService) {
        this.definitionRepository = definitionRepository;
        this.xteaRepository = xteaRepository;
        this.regionInfoRepository = regionInfoRepository;
        this.clientService = clientService;
    }

    public Map<String, Set<Xtea>> findUnique(int rev) {
        return xteaRepository.findAllByRevision(rev).stream().collect(Collectors.groupingBy(object -> String.valueOf(object.getRegion()), Collectors.toSet()));
    }

    private Map<Integer, SceneEntityDefinition> cache = new HashMap<>();
    public Optional<SceneEntityDefinition> getSceneEntityDefinition(int id) {
        return Optional.ofNullable(cache.computeIfAbsent(id, integer -> definitionRepository.findById(String.valueOf(id)).orElse(null)));
    }

    private Map<String, Region> regionCache = new HashMap<>();
    public Optional<Region> getRegion(int id) {
        File file = new File(RsEnvironment.INFO_BASE, "\\json\\regions\\" + id + ".json");
        if (!file.exists()) return Optional.empty();
        return Optional.ofNullable(regionCache.computeIfAbsent(String.valueOf(id), s -> {
            try {
                return gson.fromJson(new FileReader(file), Region.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }));
    }

    public RegionInfo save(Region region) {
        RegionInfo regionInfo = new RegionInfo();
        regionInfo.setKey(String.valueOf(region.getRegionID()));
        regionInfo.init();

        int[][][] map = new int[4][64][64];

        int[][][] doors = new int[4][64][64];
        int[][][] tileSettings = region.getTileSettings();

        for (SceneEntityInstance entityInstance : region.getLocations()) {
            int type = entityInstance.getType();
            if (type >= 4 && type <= 8) {
                continue;
            }

            SceneEntityDefinition definition = getSceneEntityDefinition(entityInstance.getId()).orElseThrow(() -> new RuntimeException("Failed to load entity def " + entityInstance.getId() + ".'"));
            Set<SceneEntityDefinition> allSceneEntityDefinitions = getAllSceneEntityDefinitions(entityInstance.getId());

            int sizeX = definition.getSizeX();
            int sizeY = definition.getSizeY();

            int plane = entityInstance.getPosition().getZ();
            int localX = entityInstance.getPosition().getX() - region.getBaseX();
            int localY = entityInstance.getPosition().getY() - region.getBaseY();

            if (plane < 4) {
                if (tileSettings != null && (tileSettings[1][localX][localY] & 2) == 2) {
                    plane--;
                }

                boolean solidMatch = allSceneEntityDefinitions.stream().anyMatch(sceneEntityDefinition -> !sceneEntityDefinition.getSolid());
                boolean impenetrableMatch = allSceneEntityDefinitions.stream().anyMatch(sceneEntityDefinition -> !sceneEntityDefinition.getImpenetrable());

                if (plane >= 0) {
                    if (type >= 0 && type <= 3) {
                        CollisionBuilder.applyWallFlags(map, plane, localX, localY, type, entityInstance.getOrientation(), solidMatch, impenetrableMatch);
                        continue;
                    }
                    if (type == 22) {
                        if (allSceneEntityDefinitions.stream().anyMatch(sceneEntityDefinition -> sceneEntityDefinition.getClipType() == 1)) {
                            CollisionBuilder.applyObjectFlag(map, plane, localX, localY);
                        }
                    } else if (type >= 9) {
                        if (allSceneEntityDefinitions.stream().anyMatch(sceneEntityDefinition -> sceneEntityDefinition.getClipType() != 0)) {


                            int orientation = entityInstance.getOrientation();
                            if (orientation != 1 && orientation != 3) {
                                CollisionBuilder.applyLargeObjectFlags(map, plane, localX, localY, sizeX, sizeY, solidMatch, impenetrableMatch);
                            } else {
                                CollisionBuilder.applyLargeObjectFlags(map, plane, localX, localY, sizeY, sizeX, solidMatch, impenetrableMatch);
                            }
                        }
                    }
                }

            }
        }

        if (tileSettings != null) {
            CollisionBuilder.applyNonLoadedFlags(tileSettings, map);
        }

        regionInfo.setRenderSettings(tileSettings);
        regionInfo.setFlags(map);
        regionInfo.setDoors(doors);

        return regionInfoRepository.save(regionInfo);
    }

    public Set<SceneEntityDefinition> getAllSceneEntityDefinitions(int id){
        Set<com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition> definitions = new HashSet<>();

        com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition sceneEntityDefinition = getSceneEntityDefinition(id).orElse(null);
        if (sceneEntityDefinition == null) throw new RuntimeException("Failed to load def for " + id + ".");
        definitions.add(sceneEntityDefinition);

        int[] transformIds = sceneEntityDefinition.getTransformIds();
        if (transformIds != null && transformIds.length > 0){
            for (int transformId : transformIds) {
                if (transformId == -1) continue;
                com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition transform = getSceneEntityDefinition(transformId).orElse(null);
                if (transform == null) throw new RuntimeException("Failed to load def for " + transformId + ".");
                definitions.add(transform);
            }
        }

        return definitions;
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
