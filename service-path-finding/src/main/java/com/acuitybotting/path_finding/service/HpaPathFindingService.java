package com.acuitybotting.path_finding.service;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.data.flow.messaging.services.client.MessagingChannel;
import com.acuitybotting.data.flow.messaging.services.client.MessagingClient;
import com.acuitybotting.data.flow.messaging.services.client.implmentation.rabbit.RabbitClient;
import com.acuitybotting.data.flow.messaging.services.client.listeners.adapters.MessagingChannelAdapter;
import com.acuitybotting.data.flow.messaging.services.client.listeners.adapters.MessagingClientAdapter;
import com.acuitybotting.db.arango.path_finding.domain.xtea.RegionMap;
import com.acuitybotting.db.arango.path_finding.domain.xtea.Xtea;
import com.acuitybotting.path_finding.algorithms.astar.AStarService;
import com.acuitybotting.path_finding.algorithms.astar.implmentation.AStarImplementation;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.graph.Node;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.PathFindingSupplier;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.TerminatingNode;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.PlayerPredicate;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.abstractions.Player;
import com.acuitybotting.path_finding.rs.custom_edges.requirements.implementations.PlayerImplementation;
import com.acuitybotting.path_finding.rs.domain.graph.TileEdge;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.EdgeType;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.service.domain.PathRequest;
import com.acuitybotting.path_finding.service.domain.PathResult;
import com.acuitybotting.path_finding.service.domain.abstractions.player.RSPlayer;
import com.acuitybotting.path_finding.web_processing.HpaWebService;
import com.acuitybotting.path_finding.xtea.XteaService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

import static com.acuitybotting.data.flow.messaging.services.client.MessagingClient.RESPONSE_QUEUE;

@Getter
@Setter
@Service
@Slf4j
public class HpaPathFindingService {

    private HPAGraph graph;

    private final XteaService xteaService;
    private final AStarService aStarService;
    private final HpaWebService hpaWebService;

    @Value("${rabbit.host}")
    private String host;

    @Value("${rabbit.username}")
    private String username;

    @Value("${rabbit.password}")
    private String password;

    @Autowired
    public HpaPathFindingService(XteaService xteaService, AStarService aStarService, HpaWebService hpaWebService) {
        this.xteaService = xteaService;
        this.aStarService = aStarService;
        this.hpaWebService = hpaWebService;
    }

    private boolean evaluateCustomEdge(HPAEdge hpaEdge, RSPlayer rsPlayer) {
        Player player = new PlayerImplementation(rsPlayer);
        Collection<PlayerPredicate> playerPredicates = hpaEdge.getCustomEdgeData().getPlayerPredicates();
        if (playerPredicates != null) {
            for (PlayerPredicate playerPredicate : playerPredicates) {
                if (!playerPredicate.test(player)) return false;
            }
        }
        return true;
    }

    public void loadRsMap() {
        log.info("Started loading RsMap this may take a few moments..");
        for (RegionMap regionMap : xteaService.getRegionMapRepository().findAll()) {
            RsEnvironment.getRsMap().getRegions().put(Integer.valueOf(regionMap.getKey()), regionMap);
        }
        log.info("Finished loading RsMap with {} regions.", RsEnvironment.getRsMap().getRegions().size());
    }

    private HPAGraph loadHpa(int version) {
        loadRsMap();
        graph = initGraph();
        hpaWebService.loadInto(graph, version, true);
        //todo graph.addCustomNodes();
        return graph;
    }

    private HPAGraph buildHpa(int version) {
        loadRsMap();
        graph = initGraph();
        graph.build();

        hpaWebService.deleteVersion(version);
        hpaWebService.save(graph, version);
        return graph;
    }

    public void consumeJobs() {
        try {
           // loadHpa(1);

            Gson outGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Gson inGson = new Gson();

            RabbitClient rabbitClient = new RabbitClient();
            rabbitClient.setVirtualHost("AcuityBotting");
            rabbitClient.auth(host, username, password);
            rabbitClient.getListeners().add(new MessagingClientAdapter() {
                @Override
                public void onConnect(MessagingClient client) {
                    MessagingChannel channel = client.createChannel();
                    channel.getListeners().add(new MessagingChannelAdapter() {
                        @Override
                        public void onConnect(MessagingChannel channel) {
                            //channel.consumeQueue("acuitybotting.work.find-path", false);
                            channel.consumeQueue("acuitybotting.work.xtea-dump", false);
                        }

                        @Override
                        public void onShutdown(MessagingChannel channel, Throwable cause) {
                            channel.connect();
                        }

                        @Override
                        public void onMessage(MessagingChannel channel, Message message) {
                            try {
                                String routing = message.getAttributes().getOrDefault("envelope.routing", "");

                                if (routing.endsWith("xtea-dump")) {
                                    int[] emptyKeys = {0, 0, 0, 0};
                                    Xtea[] xteas = inGson.fromJson(message.getBody(), Xtea[].class);
                                    for (Xtea xtea : xteas) {
                                        if (xtea.getKeys() == null || Arrays.equals(xtea.getKeys(), emptyKeys))
                                            continue;
                                        xteaService.getXteaRepository().save(xtea);
                                        log.info("Saved Xtea Key {}.", xtea);
                                    }
                                    channel.acknowledge(message);
                                } else {
                                    PathRequest pathRequest = inGson.fromJson(message.getBody(), PathRequest.class);
                                    PathResult pathResult = new PathResult();

                                    try {
                                        log.info("Finding path. {}", pathRequest);
                                        pathResult = findPath(pathRequest.getStart(), pathRequest.getEnd(), pathRequest.getRsPlayer());
                                        List<? extends Edge> path = pathResult.getPath();
                                        log.info("Found path. {}", path);

                                        pathResult.setSubPaths(new HashMap<>());
                                        if (path != null) {
                                            for (Edge edge : path) {
                                                if (edge instanceof HPAEdge) {
                                                    String pathKey = ((HPAEdge) edge).getPathKey();
                                                    List<Location> subPath = ((HPAEdge) edge).getPath();
                                                    if (pathKey != null && subPath != null) {
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
                                    channel.respond(message, json);
                                    channel.acknowledge(message);
                                }

                            } catch (Throwable e) {
                                log.error("Error during respond.", e);
                            }
                        }
                    });

                    channel.connect();
                }
            });

            rabbitClient.connect();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public PathResult findPath(Location startLocation, Location endLocation, RSPlayer rsPlayer) throws Exception {
        HPARegion startRegion = graph.getRegionContaining(startLocation);
        HPARegion endRegion = graph.getRegionContaining(endLocation);

        Objects.requireNonNull(startRegion);
        Objects.requireNonNull(endRegion);

        PathResult pathResult = new PathResult();
        if (startRegion.equals(endRegion)) {
            List<TileEdge> internalPath = graph.findInternalPath(startLocation, endLocation, startRegion, true);
            if (internalPath != null) {
                pathResult.setPath(internalPath);
                return pathResult;
            }
        }

        TerminatingNode endNode = new TerminatingNode(endRegion, endLocation);
        endNode.connectToGraph();

        TerminatingNode startNode = new TerminatingNode(startRegion, startLocation).addStartEdges();
        //todo startNode.addStartEdges();
        startNode.connectToGraph();

        try {
            AStarImplementation aStarImplementation = new AStarImplementation()
                    .setEdgePredicate(edge -> {
                        if (edge instanceof HPAEdge) {
                            HPAEdge hpaEdge = (HPAEdge) edge;
                            if (hpaEdge.getType() == EdgeType.CUSTOM && hpaEdge.getCustomEdgeData() != null && !evaluateCustomEdge(hpaEdge, rsPlayer))
                                return false;
                        }

                        Node end = edge.getEnd();
                        return !(edge instanceof TerminatingNode) || end.equals(endNode);
                    });

            List<? extends Edge> hpaPath = aStarImplementation.findPath(new LocateableHeuristic(), startNode, endNode).orElse(null);
            pathResult.setPath(hpaPath);
            pathResult.setAStarImplementation(aStarImplementation);
            return pathResult;
        } finally {
            startNode.disconnectFromGraph();
            endNode.disconnectFromGraph();
        }
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
                        ignoreStartBlocked ? Collections.singletonMap(TileNode.IGNORE_BLOCKED, start) : Collections.emptyMap()
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
}
