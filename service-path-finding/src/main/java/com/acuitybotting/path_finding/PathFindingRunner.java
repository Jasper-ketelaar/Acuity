package com.acuitybotting.path_finding;

import com.acuitybotting.path_finding.algorithms.astar.AStarService;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.PathPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapFrame;
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

    private final PathPlugin pathPlugin;

    @Autowired
    public PathFindingRunner(WebImageProcessingService webImageProcessingService, RsMapService rsMapService, AStarService aStarService, PathPlugin pathPlugin) {
        this.webImageProcessingService = webImageProcessingService;
        this.rsMapService = rsMapService;
        this.aStarService = aStarService;
        this.pathPlugin = pathPlugin;
    }

    private void dumpImage()  {
        try {
            BufferedImage image = webImageProcessingService.createDoorImage(0, 3138, 3384, 2000, 2000, 3);
            ImageIO.write(image, "png", new File("saved3.png"));
            image = null;
            System.out.println("Image dump complete.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) {
        dumpImage();
      /*  try {
            RsEnvironment.setRsMapService(rsMapService);
            MapFrame mapFrame = new MapFrame();
            mapFrame.getMapPanel().addPlugin(pathPlugin);
            mapFrame.show();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
