package com.acuitybotting.path_finding;

import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionMap;
import com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition;
import com.acuitybotting.path_finding.algorithms.astar.AStarService;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.PathFindingSupplier;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.HpaPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.PathPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.PositionPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.RegionPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapFrame;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.service.HpaPathFindingService;
import com.acuitybotting.path_finding.web_processing.HpaWebService;
import com.acuitybotting.path_finding.web_processing.WebImageProcessingService;
import com.acuitybotting.path_finding.xtea.XteaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

@Component
@Slf4j
@PropertySource("classpath:general-worker-rabbit.credentials")
public class PathFindingRunner implements CommandLineRunner {

    private final WebImageProcessingService webImageProcessingService;
    private final HpaPathFindingService hpaPathFindingService;

    @Value("${rabbit.host}")
    private String host;

    @Value("${rabbit.username}")
    private String username;

    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public PathFindingRunner(WebImageProcessingService webImageProcessingService, HpaPathFindingService hpaPathFindingService) {
        this.webImageProcessingService = webImageProcessingService;
        this.hpaPathFindingService = hpaPathFindingService;
    }

    private void exportXteas() {
        hpaPathFindingService.getXteaService().exportXteas(171, new File(RsEnvironment.INFO_BASE, "xteas.txt"));
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
            hpaPathFindingService.consumeJobs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
