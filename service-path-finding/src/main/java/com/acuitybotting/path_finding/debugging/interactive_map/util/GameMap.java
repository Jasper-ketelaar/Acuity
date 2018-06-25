package com.acuitybotting.path_finding.debugging.interactive_map.util;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Getter
public class GameMap {

    private Location base;
    private BufferedImage mapImage;

    public GameMap(String imgPath, Location base) throws IOException {
        this.base = base;
        this.mapImage = ImageIO.read(new File(imgPath));
    }
}
