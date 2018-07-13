package com.acuitybotting.path_finding;

import com.acuitybotting.data.flow.messaging.services.client.MessageConsumer;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.listener.MessagingClientAdapter;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionMap;
import com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition;
import com.acuitybotting.path_finding.algorithms.astar.AStarService;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.PathFindingSupplier;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
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
import com.acuitybotting.path_finding.service.domain.PathRequest;
import com.acuitybotting.path_finding.service.domain.PathResult;
import com.acuitybotting.path_finding.web_processing.HpaWebService;
import com.acuitybotting.path_finding.web_processing.WebImageProcessingService;
import com.acuitybotting.path_finding.xtea.XteaService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

import static com.acuitybotting.data.flow.messaging.services.client.MessagingClient.RESPONSE_QUEUE;

@Component
@Slf4j
@PropertySource("classpath:general-worker-rabbit.credentials")
public class PathFindingRunner implements CommandLineRunner {

    private final WebImageProcessingService webImageProcessingService;
    private final AStarService aStarService;

    private final PathPlugin pathPlugin;
    private final HpaPlugin hpaPlugin = new HpaPlugin();
    private final XteaService xteaService;
    private final HpaPathFindingService hpaPathFindingService;
    private final HpaWebService hpaWebService;
    private RegionPlugin regionPlugin = new RegionPlugin();

    @Value("${rabbit.host}")
    private String host;

    @Value("${rabbit.username}")
    private String username;

    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public PathFindingRunner(WebImageProcessingService webImageProcessingService, AStarService aStarService, PathPlugin pathPlugin, XteaService xteaService, HpaPathFindingService hpaPathFindingService, HpaWebService hpaWebService) {
        this.webImageProcessingService = webImageProcessingService;
        this.aStarService = aStarService;
        this.pathPlugin = pathPlugin;
        this.xteaService = xteaService;
        this.hpaPathFindingService = hpaPathFindingService;
        this.hpaWebService = hpaWebService;
    }

    private PathFindingSupplier getPathFindingSupplier() {
        return new PathFindingSupplier() {
            @Override
            public Optional<List<? extends Edge>> findPath(Location start, Location end, Predicate<Edge> predicate, boolean ignoreStartBlocked) {
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
                15,
                15
        );
        graph.setPathFindingSupplier(getPathFindingSupplier());
        return graph;
    }

    private HPAGraph loadHpa(int version) {
        loadRsMap();
        HPAGraph graph = initGraph();
        hpaWebService.loadInto(graph, version, true);
        //todo graph.addCustomNodes();
        return graph;
    }

    private HPAGraph buildHpa(int version) {
        loadRsMap();
        HPAGraph graph = initGraph();
        graph.build();

        hpaWebService.deleteVersion(version);
        hpaWebService.save(graph, version);
        return graph;
    }

    private void loadRsMap() {
        log.info("Started loading RsMap this may take a few moments..");
        for (RegionMap regionMap : xteaService.getRegionMapRepository().findAll()) {
            RsEnvironment.getRsMap().getRegions().put(Integer.valueOf(regionMap.getKey()), regionMap);
        }
        log.info("Finished loading RsMap with {} regions.", RsEnvironment.getRsMap().getRegions().size());
    }

    private void exportXteas() {
        xteaService.exportXteas(171, new File(RsEnvironment.INFO_BASE, "xteas.txt"));
    }

    private void consumeJobs(){
        try {
            loadRsMap();
            HPAGraph hpaGraph = loadHpa(1);
            hpaPathFindingService.setGraph(hpaGraph);

            Gson outGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Gson inGson = new Gson();

            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.setVirtualHost("AcuityBotting");
            rabbitClient.auth(host, username, password);
            rabbitClient.getListeners().add(new MessagingClientAdapter(){
                @Override
                public void onConnect() {
                    MessageConsumer consume = rabbitClient.consume("acuitybotting.work.find-path", false);
                    consume.withAutoAcknowledge(true);
                    consume.withCallback((messageConsumer, message) -> {
                        try {
                            PathRequest pathRequest = inGson.fromJson(message.getBody(), PathRequest.class);
                            PathResult pathResult = new PathResult();

                            try {
                                log.info("Finding path. {}", pathRequest);
                                List<? extends Edge> path = hpaPathFindingService.findPath(pathRequest.getStart(), pathRequest.getEnd());
                                log.info("Found path. {}", path);
                                pathResult.setPath(path);
                                pathResult.setSubPaths(new HashMap<>());

                                if (path != null){
                                    for (Edge edge : path) {
                                        if (edge instanceof HPAEdge){
                                            String pathKey = ((HPAEdge) edge).getPathKey();
                                            List<Location> subPath = ((HPAEdge) edge).getPath();
                                            if (pathKey != null && subPath != null){
                                                pathResult.getSubPaths().put(pathKey, subPath);
                                            }
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                log.error("Error during finding path. {}", e);
                                pathResult.setError(e.getMessage());
                            }

                            String json = outGson.toJson(pathResult);
                            log.info("Responding. {} {}", message.getAttributes().get(RESPONSE_QUEUE), json);
                            rabbitClient.respond(message, json);
                        }
                        catch (Throwable e){
                            log.error("Error during respond.", e);
                        }
                    });

                    consume.start();
                }
            });
            rabbitClient.connect();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void dump(){
        xteaService.saveRegionMapsFromAfter(171);
        webImageProcessingService.saveImagesFromRegionMaps(RsEnvironment.getRsMap().getRegions().values(), new File(RsEnvironment.INFO_BASE + "\\img\\a2_regions"));
    }

    private void openUi() throws Exception {
        MapFrame mapFrame = new MapFrame();
        mapFrame.getMapPanel().addPlugin(new PositionPlugin());
        mapFrame.getMapPanel().addPlugin(hpaPlugin);
        mapFrame.show();
    }

    private void findStairDefs(){
        String[] actionsSearch = new String[]{"Climb-up", "Climb-Up", "Climb-down", "Climb-Down", "Climb"};
        String[] namesSearch = new String[]{"Stair", "Stairs", "Ladder", "Staircase"};

        Set<SceneEntityDefinition> results = new HashSet<>();
        for (String search : actionsSearch) {
            results.addAll(xteaService.getDefinitionRepository().findAllByActionsContaining(search));
        }
        for (String search : namesSearch) {
            results.addAll(xteaService.getDefinitionRepository().findAllByNameLike(search));
        }

        Set<String> names = new HashSet<>();
        Set<String> actions = new HashSet<>();

        for (SceneEntityDefinition result : results) {
            if (result.getName() == null || result.getActions() == null) continue;
            names.add(result.getName());
            for (String action : result.getActions()) {
                if (action == null) continue;
                actions.add(action);
            }
        }

        System.out.println(names);
        System.out.println(actions);
    }

    private void findDoorDefs(){
        String[] actionsSearch = new String[]{"Open"};
        String[] namesSearch = new String[]{"Door", "Gate"};

        Set<SceneEntityDefinition> results = new HashSet<>();
        for (String search : actionsSearch) {
            results.addAll(xteaService.getDefinitionRepository().findAllByActionsContaining(search));
        }
        for (String search : namesSearch) {
            results.addAll(xteaService.getDefinitionRepository().findAllByNameLike(search));
        }

        Set<String> names = new HashSet<>();
        Set<String> actions = new HashSet<>();

        for (SceneEntityDefinition result : results) {
            if (result.getName() == null || result.getActions() == null) continue;
            names.add(result.getName());
            for (String action : result.getActions()) {
                if (action == null) continue;
                actions.add(action);
            }
        }

        System.out.println(names);
        System.out.println(actions);
    }




    @Override
    public void run(String... args) {
        try {
            dump();
            hpaPlugin.setGraph(buildHpa(1));
            openUi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
