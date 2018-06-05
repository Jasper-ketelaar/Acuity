package com.acuitybotting.path_finding.web_processing;

import com.acuitybotting.db.arango.path_finding.domain.TileFlag;
import com.acuitybotting.db.arango.path_finding.repositories.TileFlagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

/**
 * Created by Zachary Herridge on 6/5/2018.
 */
@Service
public class WebProcessingService {

    private TileFlagRepository flagRepository;

    @Autowired
    public WebProcessingService(TileFlagRepository flagRepository) {
        this.flagRepository = flagRepository;
    }

    public void createImage(){
        BufferedImage image = new BufferedImage(1000, 50, BufferedImage.TYPE_INT_ARGB);
        for (TileFlag tileFlag : flagRepository.findAll()) {

        }
    }
}
