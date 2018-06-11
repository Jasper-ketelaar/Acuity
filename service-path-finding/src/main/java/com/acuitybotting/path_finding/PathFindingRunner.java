package com.acuitybotting.path_finding;

import com.acuitybotting.path_finding.algorithms.astar.AStarService;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.rs.domain.Locateable;
import com.acuitybotting.path_finding.rs.domain.LocateableHeuristic;
import com.acuitybotting.path_finding.rs.domain.Location;
import com.acuitybotting.path_finding.rs.utils.RsEnvironmentService;
import com.acuitybotting.path_finding.web_processing.WebImageProcessingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class PathFindingRunner implements CommandLineRunner{

    private final WebImageProcessingService webImageProcessingService;
    private RsEnvironmentService rsEnvironmentService;
    private AStarService aStarService;

    @Autowired
    public PathFindingRunner(WebImageProcessingService webImageProcessingService, RsEnvironmentService rsEnvironmentService, AStarService aStarService) {
        this.webImageProcessingService = webImageProcessingService;
        this.rsEnvironmentService = rsEnvironmentService;
        this.aStarService = aStarService;
    }

    private void dumpImage() throws IOException {
        BufferedImage image = webImageProcessingService.createTileFlagImage(0, 2000, 2000, 4000, 5000, 4);
        ImageIO.write(image, "png", new File("saved3.png"));
        image = null;
        System.out.println("Image dump complete.");
    }

    private Optional<List<Edge>> findPath(Locateable start, Locateable end){
        return aStarService.findPath(
                new LocateableHeuristic(),
                RsEnvironmentService.getRsEnvironment().getNode(start.getLocation()),
                RsEnvironmentService.getRsEnvironment().getNode(end.getLocation())
        );
    }

    @Override
    public void run(String... args) {
        RsEnvironmentService.setRsEnvironment(rsEnvironmentService);
        List<Edge> edges = findPath(new Location(3207, 3502, 0), new Location(3207, 3504, 0)).orElse(null);
        System.out.println(edges);
    }
}
