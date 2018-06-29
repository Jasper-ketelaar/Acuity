package com.acuitybotting.path_finding.xtea;

import com.acuitybotting.data.flow.messaging.services.client.MessagingClientService;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionInfo;
import com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition;
import com.acuitybotting.db.arango.path_finding.domain.xtea.Xtea;
import com.acuitybotting.db.arango.path_finding.repositories.TileFlagRepository;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.RegionInfoRepository;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.SceneEntityDefinitionRepository;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.XteaRepository;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.MapFlags;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.rs.utils.RsMapService;
import com.acuitybotting.path_finding.xtea.domain.EntityLocation;
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

    private final TileFlagRepository tileFlagRepository;

    private Gson gson = new Gson();

    @Autowired
    public XteaService(SceneEntityDefinitionRepository definitionRepository, XteaRepository xteaRepository, RegionInfoRepository regionInfoRepository, MessagingClientService clientService, TileFlagRepository tileFlagRepository) {
        this.definitionRepository = definitionRepository;
        this.xteaRepository = xteaRepository;
        this.regionInfoRepository = regionInfoRepository;
        this.clientService = clientService;
        this.tileFlagRepository = tileFlagRepository;
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

    private void addFlag(EntityLocation location, int plane, int flag){
        addFlag(location.toLocation(), plane, flag);
    }

    private void addFlag(Location location, int plane, int flag){
        int regionId = RsMapService.worldToRegionId(location);
        RegionInfo regionInfo = RsEnvironment.getRegionMap().get(String.valueOf(regionId));
        if (regionInfo == null){
            log.warn("Failed to add flag to region {}.", regionId);
            return;
        }
        regionInfo.addFlag(new Location(location.getX(), location.getY(), plane), flag);
    }

    private RegionInfo createRegionInfo(Region region){
        RegionInfo regionInfo = new RegionInfo();
        regionInfo.setKey(String.valueOf(region.getRegionID()));
        regionInfo.setBaseX(region.getBaseX());
        regionInfo.setBaseY(region.getBaseY());
        int[][][] flags = new int[Region.Z][Region.X][Region.Y];
        for (int[][] planeFlags : flags) {
            for (int[] axisFlags : planeFlags) {
                Arrays.fill(axisFlags, 1);
            }
        };
        regionInfo.setFlags(flags);
        return regionInfo;
    }

    public void applySettings(int regionId){
        Region region = getRegion(regionId).orElse(null);
        if (region == null){
            log.warn("Failed to load region {}. Skipping applying flags to region.", regionId);
            return;
        }
        RegionInfo regionInfo = RsEnvironment.getRegionMap().computeIfAbsent(String.valueOf(region.getRegionID()), s -> createRegionInfo(region));

        for (int plane = 0; plane < Region.Z; plane++) {
            for (int regionX = 0; regionX < Region.X; regionX++) {
                for (int regionY = 0; regionY < Region.Y; regionY++) {
                    int setting = region.getTileSettings()[plane][regionX][regionY];

                    boolean bridge = plane + 1 < 4 && (region.getTileSettings()[plane + 1][regionX][regionY] == 2);
                    if (!bridge && setting == 1) {
                        //blocked
                        regionInfo.addFlag(regionX, regionY, plane, MapFlags.BLOCKED_SETTING);
                    }
                    else {
                        //walkable
                        regionInfo.addFlag(regionX, regionY, plane, MapFlags.OPEN_SETTINGS);
                    }
                }
            }
        }

    }

    public void applyLocations(int regionId) {
        Region region = getRegion(regionId).orElse(null);
        if (region == null){
            log.warn("Failed to load region {}. Skipping applying locations to region.", regionId);
            return;
        }

        for (SceneEntityInstance location : region.getLocations()) {
            int locationType = location.getType();

            int baseX = region.getBaseX();
            int baseY = region.getBaseY();
            int regionX = location.getPosition().getX() - baseX;
            int regionY = location.getPosition().getY() - baseY;
            int plane = location.getPosition().getZ();

            if (plane < 4) {
                if ((region.getTileSettings()[1][regionX][regionY] & 2) == 2) {//Settings that apply locations to plane below.
                    plane = plane - 1;
                }

                if (plane >= 0) {
                    addFlag(location.getPosition(), plane, MapFlags.OCCUPIED);

                    SceneEntityDefinition baseDefinition = getSceneEntityDefinition(location.getId()).orElseThrow(() -> new RuntimeException("Failed to load " + location.getId() + "."));

                    Integer clipType = baseDefinition.getClipType();
                    if (locationType == 22) {
                        if (clipType == 1) {
                            //block22 Never actually happens but client checks it.
                            addFlag(location.getPosition(), plane, MapFlags.BLOCKED_22);
                        }
                    } else {
                        if (locationType != 10 && locationType != 11) {
                            int rotation = location.getOrientation();

                            if (locationType >= 12) {
                                if (clipType != 0) {
                                    //addObject block, these are visible roofs from other rooms.
                                    addFlag(location.getPosition(), plane, MapFlags.BLOCKED_ROOF);
                                }
                            }

                            if (baseDefinition.getItemSupport() == 1) {
                                if (locationType == 0 || locationType == 2) {
                                    if (rotation == 0) {
                                        //West wall
                                        addFlag(location.getPosition(), plane, MapFlags.WALL_WEST);
                                    } else if (rotation == 1) {
                                        //North wall
                                        addFlag(location.getPosition(), plane, MapFlags.WALL_NORTH);
                                    } else if (rotation == 2) {
                                        //East wall
                                        addFlag(location.getPosition(), plane, MapFlags.WALL_EAST);
                                    } else if (rotation == 3) {
                                        //South wall
                                        addFlag(location.getPosition(), plane, MapFlags.WALL_SOUTH);
                                    }
                                }

                                if (locationType == 1) {
                                    //Wall interconnecting ignore
                                }

                                if (locationType == 2) {
                                    if (rotation == 3) {
                                        //West wall
                                        addFlag(location.getPosition(), plane, MapFlags.WALL_WEST);
                                    } else if (rotation == 0) {
                                        //North wall
                                        addFlag(location.getPosition(), plane, MapFlags.WALL_NORTH);
                                    } else if (rotation == 1) {
                                        //East wall
                                        addFlag(location.getPosition(), plane, MapFlags.WALL_EAST);
                                    } else if (rotation == 2) {
                                        //South wall
                                        addFlag(location.getPosition(), plane, MapFlags.WALL_SOUTH);
                                    }
                                }

                                if (locationType == 3) {
                                    if (rotation == 0) {
                                        //Pillar North-West
                                        addFlag(location.getPosition(), plane, MapFlags.PILLAR_NORTH_WEST);
                                    } else if (rotation == 1) {
                                        //Pillar North-East
                                        addFlag(location.getPosition(), plane, MapFlags.PILLAR_NORTH_EAST);
                                    } else if (rotation == 2) {
                                        //Pillar South-East
                                        addFlag(location.getPosition(), plane, MapFlags.PILLAR_SOUTH_EAST);
                                    } else if (rotation == 3) {
                                        //Pillar South-West
                                        addFlag(location.getPosition(), plane, MapFlags.PILLAR_SOUTH_WEST);
                                    }
                                }

                                if (locationType == 9) {
                                    int hash = (regionX << 7) + regionY + (location.getId() << 14) + 0x4000_0000;
                                    if ((hash >> 29 & 3) != 2) {
                                        continue; //Idk works
                                    }

                                    if (rotation != 0 && rotation != 2) {
                                        //North-West to South-East wall
                                        addFlag(location.getPosition(), plane, MapFlags.WALL_NORTH_WEST_TO_SOUTH_EAST);
                                    } else {
                                        //North-East to South-West wall
                                        addFlag(location.getPosition(), plane, MapFlags.WALL_NORTH_EAST_TO_SOUTH_WEST);
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
                            if (baseDefinition.getProjectileClipped() && baseDefinition.getItemSupport() == 1) {
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
                                        addFlag(location.getPosition().toLocation().clone(xOff, yOff), plane, MapFlags.BLOCKED_SCENE_OBJECT);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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
