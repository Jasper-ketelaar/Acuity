package com.acuitybotting.path_finding.rs.utils;

import com.acuitybotting.db.arango.path_finding.domain.SceneEntity;
import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.acuitybotting.db.arango.path_finding.repositories.TileFlagRepository;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */
@Service
public class RsEnvironmentService {

    private static RsEnvironmentService rsEnvironmentService;

    private final TileFlagRepository tileFlagRepository;

    public static final int PLANE_PENALTY = 25;

    @Autowired
    public RsEnvironmentService(TileFlagRepository tileFlagRepository) {
        this.tileFlagRepository = tileFlagRepository;
    }

    public TileNode getNode(Location location) {
        return getNode(location, TileNode.WALK);
    }

    public TileNode getNode(Location location, int type) {
        return new TileNode(location, type);
    }

    public Integer getFlagAt(Location location) {
        Integer integer = tileFlagRepository.findByLocation(location).map(TileFlag::getFlag).orElse(null);
        System.out.println("Found flag at: " + location + "=" + integer);
        return integer == null ? CollisionFlags.BLOCKED : integer;
    }

    public List<SceneEntity> getDoorsAt(Location location) {
        return Collections.emptyList();
    }

    public static RsEnvironmentService getRsEnvironment() {
        return rsEnvironmentService;
    }

    public static void setRsEnvironment(RsEnvironmentService rsEnvironmentService) {
        RsEnvironmentService.rsEnvironmentService = rsEnvironmentService;
    }
}
