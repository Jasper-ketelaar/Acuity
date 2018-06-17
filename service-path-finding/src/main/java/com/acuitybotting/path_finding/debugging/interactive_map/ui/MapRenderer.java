package com.acuitybotting.path_finding.debugging.interactive_map.ui;

import com.acuitybotting.path_finding.debugging.interactive_map.util.GameMap;
import com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.PositionRenderer;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MapRenderer extends JPanel implements MouseMotionListener{

    private GameMap gameMap;
    private Perspective perspective;

    private List<Plugin> plugins = new ArrayList<>();

    public MapRenderer(GameMap gameMap) {
        this.gameMap = gameMap;
        this.perspective =  new Perspective(gameMap, this);
        addMouseMotionListener(this);

        plugins.add(new PositionRenderer(this));
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        Graphics2D g1 = (Graphics2D)g.create();
        Graphics2D g2 = (Graphics2D)g.create();
        //g2.scale(scale, scale);

        Point point = perspective.locationToMap(perspective.getBase());
        g2.drawImage(this.gameMap.getMapImage(), -point.x, -point.y, this);

        for (Plugin plugin : plugins) {
            plugin.onPaint(g1, g2);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }
}
