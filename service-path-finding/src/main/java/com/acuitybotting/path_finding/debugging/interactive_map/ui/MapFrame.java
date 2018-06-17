package com.acuitybotting.path_finding.debugging.interactive_map.ui;

import com.acuitybotting.path_finding.debugging.interactive_map.util.GameMap;
import com.acuitybotting.path_finding.rs.domain.location.Location;

import javax.swing.*;
import java.awt.*;

public class MapFrame {

    private final JFrame mapFrame;
    private final MapRenderer mapRenderer;

    public MapFrame() throws Exception {
        this.mapFrame = new JFrame("Map Suite");
        this.mapFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.mapFrame.setLayout(new BorderLayout(0, 0));
        this.mapFrame.setSize(800, 600);

        this.mapRenderer = new MapRenderer(new GameMap("C:\\Users\\zgher\\IdeaProjects\\Acuity\\service-path-finding\\src\\main\\resources\\saved3.png", new Location(3138, 3384 + 2000, 0), 3));
        this.mapFrame.add(this.mapRenderer, BorderLayout.CENTER);
        this.centerFrame();
    }
    public MapRenderer getMapRenderer() {
        return mapRenderer;
    }

    public void show() {
        if (!this.mapFrame.isVisible()) {
            this.mapFrame.setVisible(true);
        }
    }

    public void hide() {
        if (this.mapFrame.isVisible()) {
            this.mapFrame.setVisible(false);
        }
    }

    public void centerFrame() {
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final int x = (toolkit.getScreenSize().width / 2) - (this.mapFrame.getWidth() / 2);
        final int y = (toolkit.getScreenSize().height / 2) - (this.mapFrame.getHeight() / 2);
        this.mapFrame.setLocation(x, y);
    }
}