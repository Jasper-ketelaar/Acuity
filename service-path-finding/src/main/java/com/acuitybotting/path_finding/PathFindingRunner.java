package com.acuitybotting.path_finding;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.acuitybotting.data.flow.messaging.services.sqs.client.SqsClientService;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionMap;
import com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition;
import com.acuitybotting.db.arango.path_finding.domain.xtea.Xtea;
import com.acuitybotting.db.arango.utils.ArangoUtils;
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
import com.acuitybotting.path_finding.web_processing.HpaWebService;
import com.acuitybotting.path_finding.web_processing.WebImageProcessingService;
import com.acuitybotting.path_finding.xtea.XteaService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PathFindingRunner implements CommandLineRunner {

    private final WebImageProcessingService webImageProcessingService;
    private final AStarService aStarService;

    private final PathPlugin pathPlugin;
    private final HpaPlugin hpaPlugin = new HpaPlugin();
    private final XteaService xteaService;
    private final HpaWebService hpaWebService;
    private RegionPlugin regionPlugin = new RegionPlugin();

    @Autowired
    public PathFindingRunner(WebImageProcessingService webImageProcessingService, AStarService aStarService, PathPlugin pathPlugin, XteaService xteaService, HpaWebService hpaWebService) {
        this.webImageProcessingService = webImageProcessingService;
        this.aStarService = aStarService;
        this.pathPlugin = pathPlugin;
        this.xteaService = xteaService;
        this.hpaWebService = hpaWebService;
    }

    private PathFindingSupplier getPathFindingSupplier() {
        return new PathFindingSupplier() {
            @Override
            public Optional<List<Edge>> findPath(Location start, Location end, Predicate<Edge> predicate, boolean ignoreStartBlocked) {
                return aStarService.findPath(
                        new LocateableHeuristic(),
                        RsEnvironment.getRsMap().getNode(start),
                        RsEnvironment.getRsMap().getNode(end),
                        predicate,
                        ignoreStartBlocked
                );
            }

            @Override
            public boolean isDirectlyConnected(Location start, Location end) {
                TileNode sNode = RsEnvironment.getRsMap().getNode(start);
                TileNode endNode = RsEnvironment.getRsMap().getNode(end);
                return sNode.getNeighbors().stream().anyMatch(edge -> edge.getEnd().equals(endNode));
            }
        };
    }

    private HPAGraph initGraph() {
        RsEnvironment.getRsMap().calculateBounds();

        HPAGraph graph = new HPAGraph();
        graph.init(
                new Location(RsEnvironment.getRsMap().getLowestX(), RsEnvironment.getRsMap().getLowestY(), 0),
                new Location(RsEnvironment.getRsMap().getHighestX(), RsEnvironment.getRsMap().getHighestY(), 3),
                30,
                30
        );
        graph.setPathFindingSupplier(getPathFindingSupplier());
        return graph;
    }

    private void loadHpa(int version) {
        HPAGraph graph = initGraph();
        hpaWebService.loadInto(graph, version, false);
        graph.addCustomNodes();
        hpaPlugin.setGraph(graph);
    }

    private void buildHpa(int version) {
        HPAGraph graph = initGraph();

        graph.build();

        hpaWebService.deleteVersion(version);
        hpaWebService.save(graph, version);

        hpaPlugin.setGraph(graph);
    }

    private void saveDefs() throws IOException {
        xteaService.getDefinitionRepository().deleteAll();
        Gson gson = new Gson();
        File[] files = new File("C:\\Users\\zgher\\Desktop\\Map Info\\json\\objects").listFiles();
        Set<SceneEntityDefinition> sceneEntityDefinitions = new HashSet<>();
        for (File child : files) {
            SceneEntityDefinition def = gson.fromJson(Files.readAllLines(child.toPath()).stream().collect(Collectors.joining("\n")), SceneEntityDefinition.class);
            for (int i = 0; i < def.getActions().length; i++) {
                if (def.getActions()[i] == null) {
                    def.getActions()[i] = "null";
                }
            }
            sceneEntityDefinitions.add(def);
        }

        ArangoUtils.saveAll(xteaService.getDefinitionRepository(), 400, sceneEntityDefinitions);

        System.out.println("Done");
    }

    private void dumpRegionImages() {
        log.info("Starting region image dump.");
        ExecutorUtil.run(30, executor -> {
            for (RegionMap regionMap : RsEnvironment.getRsMap().getRegions().values()) {
                executor.execute(() -> {
                    BufferedImage[] tileFlagImage = webImageProcessingService.createTileFlagImageFromRegionInfo(regionMap);
                    for (int i = 0; i < tileFlagImage.length; i++) {
                        try {
                            ImageIO.write(tileFlagImage[i], "png", new File(RsEnvironment.INFO_BASE, "\\img\\a_regions\\" + regionMap.getKey() + "_" + i + ".png"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        log.info("Finished region image dump.");
    }

    private void dumpRegionInfo() {
        log.info("Starting RegionMap dump.");

        xteaService.getRegionMapRepository().deleteAll();

        Set<String> regionIds = xteaService.findUnique(171).keySet();

        ExecutorUtil.run(30, executor -> {
            for (String regionId : regionIds) {
                executor.execute(() -> xteaService.applySettings(Integer.parseInt(regionId)));
            }
        });

        ExecutorUtil.run(30, executor -> {
            for (String regionId : regionIds) {
                executor.execute(() -> xteaService.applyLocations(Integer.parseInt(regionId)));
            }
        });

        for (RegionMap regionMap : RsEnvironment.getRsMap().getRegions().values()) {
            try {
                xteaService.getRegionMapRepository().save(regionMap);
            }
            catch (Exception e){
                log.error("Error during save. " + regionMap, e);
            }
        }

        log.info("Finished RegionMap dump with {} regions.", RsEnvironment.getRsMap().getRegions().size());
    }

    private void loadRsMap(){
        log.info("Started loading RsMap this may take a few moments..");
        for (RegionMap regionMap : xteaService.getRegionMapRepository().findAll()) {
            RsEnvironment.getRsMap().getRegions().put(Integer.valueOf(regionMap.getKey()), regionMap);
        }
        log.info("Finished loading RsMap with {} regions.", RsEnvironment.getRsMap().getRegions().size());
    }

    private void exportXteas(){
        xteaService.exportXteas(171, new File(RsEnvironment.INFO_BASE, "xteas.txt"));
    }

    @Override
    public void run(String... args) {
        try {
            SqsClientService sqsClientService = new SqsClientService();
            System.out.println("Send: " + sqsClientService.send("https://sqs.us-east-1.amazonaws.com/604080725100/test.fifo", "Test message"));

        /*    loadRsMap();
            buildHpa(1);

            MapFrame mapFrame = new MapFrame();
            regionPlugin.setXteaService(xteaService);
            mapFrame.getMapPanel().addPlugin(new PositionPlugin());
            mapFrame.getMapPanel().addPlugin(hpaPlugin);
            mapFrame.show();*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
