package com.acuitybotting.path_finding;

import com.acuitybotting.db.arango.path_finding.repositories.TileFlagRepository;
import com.acuitybotting.path_finding.tile_capture.domain.TileCaptureCheck;
import com.acuitybotting.path_finding.tile_capture.service.TileCaptureService;
import com.acuitybotting.path_finding.web_processing.WebProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Component
public class PathFindingRunner implements CommandLineRunner{

    private final TileCaptureService tileCaptureService;
    private final WebProcessingService webProcessingService;

    @Autowired
    public PathFindingRunner(TileFlagRepository tileFlagRepository, TileCaptureService tileCaptureService, WebProcessingService webProcessingService) {
        this.tileCaptureService = tileCaptureService;
        this.webProcessingService = webProcessingService;
    }

    @Override
    public void run(String... args) throws Exception {
/*        BufferedImage image = webProcessingService.createImage(0, 4000, 5000, 4);
        ImageIO.write(image, "png", new File("saved2.png"));*/
    }
}
