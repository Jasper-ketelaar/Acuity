package com.acuitybotting.path_finding.debugging.interactive_map;

import com.acuitybotting.path_finding.rs.domain.location.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MapRenderer extends JPanel implements MouseMotionListener{

    private GameMap gameMap;
    private Perspective perspective;

    public MapRenderer(GameMap gameMap) {
        this.gameMap = gameMap;
        this.perspective =  new Perspective(gameMap, this);
        addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g.create();
        //g2.scale(scale, scale);

        Point point = perspective.locationToMap(perspective.getBase());
        g2.drawImage(this.gameMap.getMapImage(), -point.x, -point.y, this);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }
}
