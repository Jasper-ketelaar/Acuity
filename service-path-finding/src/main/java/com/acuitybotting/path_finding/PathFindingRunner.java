package com.acuitybotting.path_finding;

import com.acuitybotting.path_finding.algorithms.astar.AStarService;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.PathFindingSupplier;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.HpaPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.PathPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapFrame;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.rs.utils.RsMapService;
import com.acuitybotting.path_finding.web_processing.HpaWebService;
import com.acuitybotting.path_finding.web_processing.WebImageProcessingService;
import com.acuitybotting.path_finding.xtea.XteaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class PathFindingRunner implements CommandLineRunner {

    private final WebImageProcessingService webImageProcessingService;
    private final RsMapService rsMapService;
    private final AStarService aStarService;

    private final PathPlugin pathPlugin;
    private final HpaPlugin hpaPlugin = new HpaPlugin();

    private final XteaService xteaService;

    private final HpaWebService hpaWebService;

    @Autowired
    public PathFindingRunner(WebImageProcessingService webImageProcessingService, RsMapService rsMapService, AStarService aStarService, PathPlugin pathPlugin, XteaService xteaService, HpaWebService hpaWebService) {
        this.webImageProcessingService = webImageProcessingService;
        this.rsMapService = rsMapService;
        this.aStarService = aStarService;
        this.pathPlugin = pathPlugin;
        this.xteaService = xteaService;
        this.hpaWebService = hpaWebService;
    }

    private void dumpImage() {
        try {
            System.out.println("Started image dump.");
            BufferedImage image = webImageProcessingService.createDoorImage(
                    0, 3138 - 1000, 3384 - 1000,
                    2000, 2000,
                    4);
            ImageIO.write(image, "png", new File("saved3.png"));
            image = null;
            System.out.println("Image dump complete.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PathFindingSupplier getPathfindingSupplier() {
        return new PathFindingSupplier() {
            @Override
            public Optional<List<Edge>> findPath(Location start, Location end, Predicate<Edge> predicate) {
                return aStarService.findPath(
                        new LocateableHeuristic(),
                        RsEnvironment.getNode(start),
                        RsEnvironment.getNode(end),
                        predicate
                );
            }

            @Override
            public boolean isDirectlyConnected(Location start, Location end) {
                TileNode sNode = RsEnvironment.getNode(start);
                TileNode endNode = RsEnvironment.getNode(end);
                return sNode.getNeighbors().stream().anyMatch(edge -> edge.getEnd().equals(endNode));
            }
        };
    }

    private HPAGraph initGraph(){
        HPAGraph graph = new HPAGraph();
        graph.init(
                new Location(3138 - 1000, 3384 - 1000, 0),
                new Location(3138 + 1000, 3384 + 1000, 1),
                30,
                30
        );
        graph.setPathFindingSupplier(getPathfindingSupplier());
        return graph;
    }

    private void loadHpa(int version) {
        HPAGraph graph = initGraph();
        hpaWebService.loadInto(graph, version);
        graph.addCustomNodes();
        hpaPlugin.setGraph(graph);
    }

    private void buildHpa(int version) {
        HPAGraph graph = initGraph();

        graph.build();

        hpaWebService.deleteVersion(version);
        hpaWebService.save(graph, version);
    }

    @Override
    public void run(String... args) {
        try {
     /*       RsEnvironment.setRsMapService(rsMapService);
            MapFrame mapFrame = new MapFrame();
            mapFrame.getMapPanel().addPlugin(hpaPlugin);
            mapFrame.show();
            loadHpa(1);*/

     xteaService.consumeQueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
