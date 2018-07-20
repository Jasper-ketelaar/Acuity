package com.acuitybotting.path_finding;

import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.PositionPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapFrame;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.service.HpaPathFindingService;
import com.acuitybotting.path_finding.web_processing.WebImageProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class PathFindingRunner implements CommandLineRunner {

    private final WebImageProcessingService webImageProcessingService;
    private final HpaPathFindingService hpaPathFindingService;

    @Autowired
    public PathFindingRunner(WebImageProcessingService webImageProcessingService, HpaPathFindingService hpaPathFindingService) {
        this.webImageProcessingService = webImageProcessingService;
        this.hpaPathFindingService = hpaPathFindingService;
    }

    private void exportXteas() {
        hpaPathFindingService.getXteaService().exportXteasGreaterThanRev(171, new File(RsEnvironment.INFO_BASE, "xteas.txt"));
    }

    private void dump() {
        hpaPathFindingService.getXteaService().saveRegionMapsFromAfter(171);
        webImageProcessingService.saveImagesFromRegionMaps(RsEnvironment.getRsMap().getRegions().values(), new File(RsEnvironment.INFO_BASE + "\\img\\a2_regions"));
    }

    private void openUi() throws Exception {
        MapFrame mapFrame = new MapFrame();
        mapFrame.getMapPanel().addPlugin(new PositionPlugin());
        mapFrame.show();
    }

    @Override
    public void run(String... args) {
        try {
            dump();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
