package com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl;

import com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.RegionUtils;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.xtea.XteaService;
import com.acuitybotting.path_finding.xtea.domain.rs.cache.RsRegion;
import com.acuitybotting.path_finding.xtea.domain.rs.cache.RsLocation;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class RegionPlugin extends Plugin {

    private XteaService xteaService;

    private static String defToDebug(SceneEntityDefinition baseDef) {
        String result = "";
        result += "CT:" + baseDef.getClipType() + " ";
        result += baseDef.getProjectileClipped() ? "P" : "_";
        result += baseDef.getSolid() ? "S" : "_";
        result += baseDef.getImpenetrable() ? "I" : "_";
        result += baseDef.getClipped() ? "C" : "_";

        result += " " + baseDef.getMapSceneId();
        result += " " + baseDef.getMapFunction();
        result += " " + baseDef.getItemSupport();

        result += "   " + baseDef.getVarpbitIndex();
        result += " " + baseDef.getVarpIndex();

        return result;
    }

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
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void onPaint(Graphics2D graphics) {
        Location location = getPerspective().screenToLocation(this.getMapPanel().getMousePosition());

        int regionId = RegionUtils.locationToRegionId(location);
        RsRegion rsRegion = xteaService.getRegion(regionId).orElse(null);

        List<String> locationDebugs = new ArrayList<>();
        int setting = -404;
        if (rsRegion != null) {
            String ids = "Id: " +
                    rsRegion.getAt(rsRegion.getOverlayIds(), location) + " " +
                    rsRegion.getAt(rsRegion.getOverlayPaths(), location) + " " +
                    rsRegion.getAt(rsRegion.getOverlayRotations(), location) + " " +
                    rsRegion.getAt(rsRegion.getUnderlayIds(), location) + " " +
                    rsRegion.getAt(rsRegion.getTileHeights(), location);
            getPaintUtil().debug(ids);

            setting = rsRegion.getTileSetting(location);

            Collection<RsLocation> instancesAt = rsRegion.getInstancesAt(location);
            instancesAt.stream().sorted(Comparator.comparingInt(RsLocation::getType)).forEach(rsLocation -> {
                SceneEntityDefinition baseDef = xteaService.getSceneEntityDefinition(rsLocation.getId()).orElse(null);
                if (baseDef != null) {
                    locationDebugs.add(baseDef.getName() + "/" + baseDef.getIdentifier() + ": OT:" + rsLocation.getType() + " " + defToDebug(baseDef));

                    Integer[] transformIds = baseDef.getTransformIds();
                    if (transformIds == null) return;
                    for (int transformId : transformIds) {
                        xteaService.getSceneEntityDefinition(transformId).ifPresent(subDef -> locationDebugs.add("  " + subDef.getName() + "/" + subDef.getIdentifier() + " " + defToDebug(baseDef)));
                    }

                }
            });
        }

        getPaintUtil().debug("Setting: " + setting);
        locationDebugs.forEach(s -> getPaintUtil().debug(s));

        getPaintUtil().markLocation(graphics, location, Color.RED);

        if (location != null){
            getPaintUtil().connectLocations(graphics, RsEnvironment.getRsMap().getNode(location).getNeighbors(), Color.RED);
        }
    }
}
