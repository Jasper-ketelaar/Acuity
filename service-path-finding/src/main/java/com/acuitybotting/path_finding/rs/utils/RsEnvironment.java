package com.acuitybotting.path_finding.rs.utils;

import com.acuitybotting.db.arango.path_finding.domain.SceneEntity;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import java.util.List;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */
public class RsEnvironment {

    public static final int PLANE_PENALTY = 25;

    public static final String[] DOOR_NAMES = new String[]{"Door", "Gate", "Large door", "Castle door", "Gate of War", "Rickety door", "Oozing barrier", "Portal of Death", "Magic guild door", "Prison door", "Barbarian door"};

    private static RsMapService rsMapService;

    public static TileNode getNode(Location location) {
        return rsMapService.getNode(location);
    }

    public static TileNode getNode(Location location, int type) {
        return rsMapService.getNode(location, type);
    }

    public static Integer getFlagAt(Location location) {
        return rsMapService.getFlagAt(location);
    }

    public static List<SceneEntity> getDoorsAt(Location location) {
        return rsMapService.getDoorsAt(location);
    }

    public static RsMapService getRsMapService() {
        return rsMapService;
    }

    public static void setRsMapService(RsMapService rsMapService) {
        RsEnvironment.rsMapService = rsMapService;
    }
}
