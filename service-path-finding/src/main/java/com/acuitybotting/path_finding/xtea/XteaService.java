package com.acuitybotting.path_finding.xtea;


import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionMap;
import com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition;
import com.acuitybotting.db.arango.path_finding.domain.xtea.Xtea;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.RegionMapRepository;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.SceneEntityDefinitionRepository;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.XteaRepository;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.MapFlags;
import com.acuitybotting.path_finding.rs.utils.HpaGenerationData;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.xtea.domain.rs.cache.RsLocationPosition;
import com.acuitybotting.path_finding.xtea.domain.rs.cache.RsRegion;
import com.acuitybotting.path_finding.xtea.domain.rs.cache.RsLocation;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 6/22/2018.
 */
@Service
@Getter
@Slf4j
public class XteaService {

    private final SceneEntityDefinitionRepository definitionRepository;
    private final XteaRepository xteaRepository;

    private final RegionMapRepository regionMapRepository;

    private Map<Integer, SceneEntityDefinition> sceneEntityCache = new HashMap<>();
    private Map<String, RsRegion> regionCache = new HashMap<>();

    private Gson gson = new Gson();

    @Autowired
    public XteaService(SceneEntityDefinitionRepository definitionRepository, XteaRepository xteaRepository, RegionMapRepository regionMapRepository) {
        this.definitionRepository = definitionRepository;
        this.xteaRepository = xteaRepository;
        this.regionMapRepository = regionMapRepository;
    }

    public void saveRegionMapsFromAfter(int revision){
        log.info("Starting RegionMap dump.");

        getRegionMapRepository().deleteAll();

        Set<String> regionIds = findUniqueAfter(revision).keySet();

        ExecutorUtil.run(30, executor -> {
            for (String regionId : regionIds) {
                executor.execute(() -> applySettings(Integer.parseInt(regionId)));
            }
        });

        ExecutorUtil.run(30, executor -> {
            for (String regionId : regionIds) {
                executor.execute(() -> applyLocations(Integer.parseInt(regionId)));
            }
        });

        for (RegionMap regionMap : RsEnvironment.getRsMap().getRegions().values()) {
            try {
                getRegionMapRepository().save(regionMap);
            } catch (Throwable e) {
                log.error("Error during save. " + regionMap, e);
            }
        }

        log.info("Finished RegionMap dump with {} regions.", RsEnvironment.getRsMap().getRegions().size());
    }

    private Map<String, Set<Xtea>> findUniqueAfter(int rev) {
        return xteaRepository.findAllByRevisionGreaterThanEqual(rev).stream().collect(Collectors.groupingBy(object -> String.valueOf(object.getRegion()), Collectors.toSet()));
    }

    public void exportXteasGreaterThanRev(int rev, File out) {
        Map<String, Set<Xtea>> uniqueAfter = findUniqueAfter(rev);
        Set<Map.Entry<String, Set<Xtea>>> keySets = uniqueAfter.entrySet();

        StringJoiner stringJoiner = new StringJoiner("\n");
        for (Map.Entry<String, Set<Xtea>> keySetEntry : keySets) {
            StringBuilder result = new StringBuilder(keySetEntry.getKey());
            for (Xtea xtea : keySetEntry.getValue()) {
                result.append(" ").append(Arrays.stream(xtea.getKeys()).mapToObj(String::valueOf).collect(Collectors.joining(",")));
            }
            stringJoiner.add(result.toString());
        }
        log.info("Exported {} xteas to file {}.", keySets.size(), out);

        try {
            Files.write(new File(out, "xteas.txt").toPath(), stringJoiner.toString().getBytes());
            Files.write(new File(out, "xteas.json").toPath(), gson.toJson(uniqueAfter).getBytes());
        } catch (IOException e) {
            log.error("Error during exporting xteas.", e);
        }
    }

    public Optional<SceneEntityDefinition> getSceneEntityDefinition(int id) {
        return Optional.ofNullable(sceneEntityCache.computeIfAbsent(id, integer -> definitionRepository.findByKey(id).orElse(null)));
    }

    public Optional<RsRegion> getRegion(int id) {
        File file = new File(RsEnvironment.INFO_BASE, "\\json\\regions\\" + id + ".json");
        if (!file.exists()) return Optional.empty();
        return Optional.ofNullable(regionCache.computeIfAbsent(String.valueOf(id), s -> {
            try {
                return gson.fromJson(new FileReader(file), RsRegion.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }));
    }

    private void addFlag(RsLocationPosition location, int plane, int flag) {
        addFlag(location.toLocation(), plane, flag);
    }

    private void addFlag(Location location, int plane, int flag) {
        RegionMap regionMap = RsEnvironment.getRsMap().getRegion(location).orElse(null);
        if (regionMap == null) {
            log.warn("Failed to add flag to {}.", location);
            return;
        }
        regionMap.addFlag(new Location(location.getX(), location.getY(), plane), flag);
    }

    private RegionMap createRegionInfo(RsRegion rsRegion) {
        RegionMap regionMap = new RegionMap();
        regionMap.setKey(String.valueOf(rsRegion.getRegionID()));
        regionMap.setBaseX(rsRegion.getBaseX());
        regionMap.setBaseY(rsRegion.getBaseY());
        int[][][] flags = new int[RsRegion.Z][RsRegion.X][RsRegion.Y];
        for (int[][] planeFlags : flags) {
            for (int[] axisFlags : planeFlags) {
                Arrays.fill(axisFlags, 1);
            }
        }
        regionMap.setFlags(flags);
        return regionMap;
    }

    public void applySettings(int regionId) {
        RsRegion rsRegion = getRegion(regionId).orElse(null);
        if (rsRegion == null) {
            log.warn("Failed to load rsRegion {}. Skipping applying flags to rsRegion.", regionId);
            return;
        }
        RegionMap regionMap = RsEnvironment.getRsMap().getRegions().computeIfAbsent(rsRegion.getRegionID(), s -> createRegionInfo(rsRegion));

        for (int plane = 0; plane < RsRegion.Z; plane++) {
            for (int regionX = 0; regionX < RsRegion.X; regionX++) {
                for (int regionY = 0; regionY < RsRegion.Y; regionY++) {
                    int setting = rsRegion.getTileSettings()[plane][regionX][regionY];

                    boolean bridge = plane + 1 < 4 && (rsRegion.getTileSettings()[plane + 1][regionX][regionY] == 2);
                    if (!bridge && setting == 1  || HpaGenerationData.isBlocked(regionMap.getBaseX() + regionX, regionMap.getBaseY() + regionY, plane)) {
                        //blocked
                        regionMap.addFlag(regionX, regionY, plane, MapFlags.BLOCKED_SETTING);
                    } else {
                        //walkable
                        regionMap.addFlag(regionX, regionY, plane, MapFlags.OPEN_SETTINGS);
                    }

                    if (rsRegion.getOverlayIds()[plane][regionX][regionY] == 0){
                        regionMap.addFlag(regionX, regionY, plane, MapFlags.NO_OVERLAY);
                    }
                }
            }
        }
    }

    public void applyLocations(int regionId) {
        RsRegion rsRegion = getRegion(regionId).orElse(null);
        if (rsRegion == null) {
            log.warn("Failed to load rsRegion {}. Skipping applying locations to rsRegion.", regionId);
            return;
        }

        for (RsLocation location : rsRegion.getLocations()) {
            int locationType = location.getType();

            int baseX = rsRegion.getBaseX();
            int baseY = rsRegion.getBaseY();
            int regionX = location.getPosition().getX() - baseX;
            int regionY = location.getPosition().getY() - baseY;
            int plane = location.getPosition().getZ();

            if (plane < 4) {
                if ((rsRegion.getTileSettings()[1][regionX][regionY] & 2) == 2) {//Settings that apply locations to plane below.
                    plane = plane - 1;
                }

                if (plane >= 0) {
                    addFlag(location.getPosition(), plane, MapFlags.OCCUPIED);

                    SceneEntityDefinition baseDefinition = getSceneEntityDefinition(location.getId()).orElseThrow(() -> new RuntimeException("Failed to load " + location.getId() + "."));

                    boolean doorFlag = HpaGenerationData.isDoor(location.getPosition().toLocation(), baseDefinition.getName(), baseDefinition.getActions(), baseDefinition.getMapDoorFlag());

                    if (doorFlag){
                        addFlag(location.getPosition(), plane, MapFlags.DOOR_FLAG);
                    }

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

                            if (!doorFlag && baseDefinition.getItemSupport() == 1) {
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
                                    addFlag(location.getPosition(), plane, MapFlags.WALL_TYPE_1);
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
                        } else {
                            if (baseDefinition.getItemSupport() == 1) {
                                //addObject blocks walking

                                int width;
                                int length;

                                int orientation = location.getOrientation();
                                if (orientation != 1 && orientation != 3) {
                                    width = baseDefinition.getSizeX();
                                    length = baseDefinition.getSizeY();
                                } else {
                                    width = baseDefinition.getSizeY();
                                    length = baseDefinition.getSizeX();
                                }

                                boolean override = false;

                                boolean up = HpaGenerationData.isPositivePlaneChange(baseDefinition.getName(), baseDefinition.getActions(), baseDefinition.getKey());
                                boolean down = HpaGenerationData.isNegativePlaneChange(baseDefinition.getName(), baseDefinition.getActions(), baseDefinition.getKey());

                                for (int xOff = 0; xOff < width; xOff++) {
                                    for (int yOff = 0; yOff < length; yOff++) {
                                        addFlag(location.getPosition().toLocation().clone(xOff, yOff), plane, MapFlags.BLOCKED_SCENE_OBJECT);

                                        if (override) addFlag(location.getPosition().toLocation().clone(xOff, yOff), plane, MapFlags.OPEN_OVERRIDE);

                                        if (up || down) {
                                            addFlag(location.getPosition().toLocation().clone(xOff, yOff), plane, MapFlags.OPEN_OVERRIDE_END);
                                            addFlag(location.getPosition().toLocation().clone(xOff, yOff), plane, MapFlags.OPEN_OVERRIDE_START);
                                        }
                                        if (up) addFlag(location.getPosition().toLocation().clone(xOff, yOff), plane, MapFlags.PLANE_CHANGE_UP);
                                        if (down) addFlag(location.getPosition().toLocation().clone(xOff, yOff), plane, MapFlags.PLANE_CHANGE_DOWN);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
