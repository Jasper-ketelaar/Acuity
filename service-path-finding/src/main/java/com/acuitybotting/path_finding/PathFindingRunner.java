package com.acuitybotting.path_finding;

import com.acuitybotting.db.arango.path_finding.repositories.TileFlagRepository;
import com.acuitybotting.path_finding.tile_capture.service.TileCaptureService;
import com.acuitybotting.path_finding.web_processing.WebImageProcessingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Component
public class PathFindingRunner implements CommandLineRunner{

    private final TileFlagRepository tileFlagRepository;
    private final TileCaptureService tileCaptureService;
    private final WebImageProcessingService webImageProcessingService;

    @Autowired
    public PathFindingRunner(TileFlagRepository tileFlagRepository, TileCaptureService tileCaptureService, WebImageProcessingService webImageProcessingService) {
        this.tileFlagRepository = tileFlagRepository;
        this.tileCaptureService = tileCaptureService;
        this.webImageProcessingService = webImageProcessingService;
    }

    @Override
    public void run(String... args) throws Exception {
/*        long start = System.currentTimeMillis();
        long l = tileFlagRepository.countAllByXBetweenAndYBetweenAndPlane(3200, 3300, 3300, 3400, 0);
        long finish = System.currentTimeMillis();
        System.out.println("Count: " + l + " in " + (finish - start) + " ms.");*/

        BufferedImage image = webImageProcessingService.createTileFlagImage(0, 2000, 2000, 4000, 5000, 4);
        ImageIO.write(image, "png", new File("saved3.png"));
        image = null;
        System.out.println("Image dump complete.");
    }
}
