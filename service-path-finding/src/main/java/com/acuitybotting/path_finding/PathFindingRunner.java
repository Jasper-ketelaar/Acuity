package com.acuitybotting.path_finding;

import com.acuitybotting.data.flow.messaging.services.aws.iot.IotClientService;
import com.acuitybotting.data.flow.messaging.services.aws.iot.client.IotClientListener;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionMap;
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

import static com.acuitybotting.data.flow.messaging.services.interfaces.MessagingClient.RESPONSE_TOPIC;

@Component
@Slf4j
@PropertySource("classpath:iot.credentials")
public class PathFindingRunner implements CommandLineRunner {

    private final WebImageProcessingService webImageProcessingService;
    private final AStarService aStarService;

    private final PathPlugin pathPlugin;
    private final HpaPlugin hpaPlugin = new HpaPlugin();
    private final XteaService xteaService;
    private final HpaPathFindingService hpaPathFindingService;
    private final HpaWebService hpaWebService;
    private RegionPlugin regionPlugin = new RegionPlugin();

    @Value("${iot.access}")
    private String access;

    @Value("${iot.secret}")
    private String secret;

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
                15,
                15
        );
        graph.setPathFindingSupplier(getPathFindingSupplier());
        return graph;
    }

    private HPAGraph loadHpa(int version) {
        HPAGraph graph = initGraph();
        hpaWebService.loadInto(graph, version, false);
        //todo graph.addCustomNodes();
        return graph;
    }

    private HPAGraph buildHpa(int version) {
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
            String clientEndpoint = "a2i158467e5k2v.iot.us-east-1.amazonaws.com";
            String clientId = "pathing-worker-1";

            IotClientService iotClientService = new IotClientService();

            iotClientService.auth(clientEndpoint, clientId, access, secret, null);
            iotClientService.getClient().getListeners().add(new IotClientListener() {
                @Override
                public void onConnect() {
                    try {
                        log.info("Consuming web-find-path queue.");
                        iotClientService.consume("user/+/connection/+/services/webFindPath")
                                .withCallback(message -> {
                                    try {
                                        PathRequest pathRequest = inGson.fromJson(message.getBody(), PathRequest.class);
                                        PathResult pathResult = new PathResult();

                                        try {
                                            log.info("Finding path. {}", pathRequest);
                                            List<Edge> path = hpaPathFindingService.findPath(pathRequest.getStart(), pathRequest.getEnd());
                                            log.info("Found path. {}", path);
                                            pathResult.setPath(path);
                                        } catch (Exception e) {
                                            log.info("Error during finding path. {}", e.getMessage());
                                            pathResult.setError(e.getMessage());
                                        }

                                        String json = outGson.toJson(pathResult);
                                        log.info("Responding. {} {}", message.getAttributes().get(RESPONSE_TOPIC), json);
                                        iotClientService.respond(message, json);
                                    }
                                    catch (Throwable e){
                                        log.error("Error during respond.", e);
                                    }
                                })
                                .start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionFailure() {

                }

                @Override
                public void onConnectionClosed() {

                }
            });

            iotClientService.connect();

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private void dump(){
        xteaService.saveRegionMapsFromAfter(171);
        webImageProcessingService.saveImagesFromRegionMaps(RsEnvironment.getRsMap().getRegions().values(), new File("C:\\Users\\zgher\\Desktop\\Map Info\\img\\a2_regions"));
    }

    private void openUi() throws Exception {
        MapFrame mapFrame = new MapFrame();
        mapFrame.getMapPanel().addPlugin(new PositionPlugin());
        mapFrame.getMapPanel().addPlugin(hpaPlugin);
        mapFrame.show();

    }

    @Override
    public void run(String... args) {
        try {
            loadRsMap();
            hpaPlugin.setGraph(loadHpa(1));
            openUi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
