package com.acuitybotting.path_finding;

import com.acuitybotting.path_finding.algorithms.astar.AStarService;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.HpaService;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.PathFindingSupplier;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.HpaPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.PathPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapFrame;
import com.acuitybotting.path_finding.rs.domain.graph.TileNode;
import com.acuitybotting.path_finding.rs.domain.location.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.rs.utils.RsMapService;
import com.acuitybotting.path_finding.web_processing.WebImageProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component
public class PathFindingRunner implements CommandLineRunner {

    private final WebImageProcessingService webImageProcessingService;
    private final RsMapService rsMapService;
    private final HpaService hpaService;
    private final AStarService aStarService;

    private final PathPlugin pathPlugin;
    private final HpaPlugin hpaPlugin = new HpaPlugin();

    @Autowired
    public PathFindingRunner(WebImageProcessingService webImageProcessingService, RsMapService rsMapService, HpaService hpaService, AStarService aStarService, PathPlugin pathPlugin) {
        this.webImageProcessingService = webImageProcessingService;
        this.rsMapService = rsMapService;
        this.hpaService = hpaService;
        this.aStarService = aStarService;
        this.pathPlugin = pathPlugin;
    }

    private void dumpImage() {
        try {
            BufferedImage image = webImageProcessingService.createDoorImage(0, 3138, 3384, 2000, 2000, 3);
            ImageIO.write(image, "png", new File("saved3.png"));
            image = null;
            System.out.println("Image dump complete.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildHpa() {
        hpaService.setDimensions(50, 50);

        Map<String, HPARegion> graph = hpaService.getHPAGraphBuilder().init(
                new Location(3138, 3384, 0),
                new Location(3138 + 300, 3384 + 300, 0)
        );

        hpaPlugin.setGraph(graph);

        hpaService.getHPAGraphBuilder().build(
                new PathFindingSupplier() {
                    @Override
                    public int findPath(Location start, Location end, Predicate<Edge> predicate) {
                        return aStarService.findPath(
                                new LocateableHeuristic(),
                                RsEnvironment.getNode(start),
                                RsEnvironment.getNode(end),
                                predicate
                        ).map(List::size).orElse(0);
                    }

                    @Override
                    public boolean isDirectlyConnected(Location start, Location end) {
                        TileNode sNode = RsEnvironment.getNode(start);
                        TileNode endNode = RsEnvironment.getNode(end);
                        return sNode.getNeighbors().stream().anyMatch(edge -> edge.getEnd().equals(endNode));
                    }
                }
        );
    }

    @Override
    public void run(String... args) {
        RsEnvironment.setRsMapService(rsMapService);
        aStarService.setDebugMode(true);

        try {
            MapFrame mapFrame = new MapFrame();
            mapFrame.getMapPanel().addPlugin(hpaPlugin);
            mapFrame.getMapPanel().addPlugin(pathPlugin);
            mapFrame.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        buildHpa();
    }
}
