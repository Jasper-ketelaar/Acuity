package com.acuitybotting.path_finding.rs.utils;

import com.acuitybotting.db.arango.path_finding.domain.SceneEntity;
import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.acuitybotting.db.arango.path_finding.repositories.TileFlagRepository;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */

@Service
public class RsMapService {

    private final TileFlagRepository tileFlagRepository;

    @Autowired
    public RsMapService(TileFlagRepository tileFlagRepository) {
        this.tileFlagRepository = tileFlagRepository;
    }

    public TileNode getNode(Location location) {
        return getNode(location, TileNode.WALK);
    }

    public TileNode getNode(Location location, int type) {
        return new TileNode(location, type);
    }

    public Integer getFlagAt(Location location) {
        Integer flag = tileFlagRepository.findByLocation(location).map(TileFlag::getFlag).orElse(null);
        return flag == null ? CollisionFlags.BLOCKED : flag;
    }

    public TileFlagRepository getTileFlagRepository() {
        return tileFlagRepository;
    }

    public List<SceneEntity> getDoorsAt(Location location) {
        return Collections.emptyList();
    }
}
