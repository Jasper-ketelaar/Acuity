package com.acuitybotting.path_finding;

import com.acuitybotting.path_finding.algorithms.astar.AStarService;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.rs.domain.location.Locateable;
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
import java.util.Optional;

@Component
public class PathFindingRunner implements CommandLineRunner{

    private final WebImageProcessingService webImageProcessingService;
    private RsMapService rsMapService;
    private AStarService aStarService;

    @Autowired
    public PathFindingRunner(WebImageProcessingService webImageProcessingService, RsMapService rsMapService, AStarService aStarService) {
        this.webImageProcessingService = webImageProcessingService;
        this.rsMapService = rsMapService;
        this.aStarService = aStarService;
    }

    private void dumpImage()  {
        try {
            BufferedImage image = webImageProcessingService.createDoorImage(0, 2000, 2000, 4000, 5000, 4);
            ImageIO.write(image, "png", new File("saved3.png"));
            image = null;
            System.out.println("Image dump complete.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<List<Edge>> findPath(Locateable start, Locateable end){
        return aStarService.findPath(
                new LocateableHeuristic(),
                RsEnvironment.getNode(start.getLocation()),
                RsEnvironment.getNode(end.getLocation())
        );
    }

    private void findPath(){
        RsEnvironment.setRsMapService(rsMapService);
        List<Edge> edges = findPath(new Location(3207, 3502, 0), new Location(3207, 3504, 0)).orElse(null);
        System.out.println(edges);
    }

    @Override
    public void run(String... args) {
        findPath();
    }
}
