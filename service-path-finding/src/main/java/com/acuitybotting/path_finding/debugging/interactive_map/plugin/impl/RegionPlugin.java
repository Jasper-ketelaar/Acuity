package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.RsMapService;
import com.acuitybotting.path_finding.xtea.XteaService;
import com.acuitybotting.path_finding.xtea.domain.Region;
import com.acuitybotting.path_finding.xtea.domain.SceneEntityInstance;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RegionPlugin extends Plugin{

    private XteaService xteaService;

    public void setXteaService(XteaService xteaService) {
        this.xteaService = xteaService;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void onPaint(Graphics2D graphics) {
        Location location = getPerspective().screenToLocation(this.getMapPanel().getMousePosition());
        int regionId = RsMapService.worldToRegionId(location);
        Optional<Region> region = xteaService.getRegion(regionId);
        List<Integer> collect = region.map(region1 -> region1.getInstancesAt(location)).orElse(Collections.emptyList()).stream().mapToInt(SceneEntityInstance::getType).distinct().boxed().collect(Collectors.toList());
        getPaintUtil().debug("RTypes: " + collect);
        getPaintUtil().markLocation(graphics, location, Color.RED);
    }
}
