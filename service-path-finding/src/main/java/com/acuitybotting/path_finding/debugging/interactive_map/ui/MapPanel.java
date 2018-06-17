package com.acuitybotting.path_finding.debugging.interactive_map.ui;

import com.acuitybotting.path_finding.debugging.interactive_map.util.GameMap;
import com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.PositionPlugin;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
public class MapPanel extends JPanel implements MouseMotionListener, MouseListener {

    private GameMap gameMap;
    private Perspective perspective;

    private List<Plugin> plugins = new ArrayList<>();


    private Point mouseStartDragPoint = null;
    private Point mouseCurrentDragPoint = null;

    public MapPanel(GameMap gameMap) {
        this.gameMap = gameMap;
        this.perspective =  new Perspective(gameMap, this);

        addMouseMotionListener(this);
        addMouseListener(this);

        plugins.add(new PositionPlugin(this));

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::handleDrag, 100, 120, TimeUnit.MILLISECONDS);
    }

    private void handleDrag(){
        if (mouseStartDragPoint != null && mouseCurrentDragPoint != null){
            int xDif = mouseCurrentDragPoint.x - mouseStartDragPoint.x;
            int yDif = mouseStartDragPoint.y - mouseCurrentDragPoint.y;
            perspective.getBase().transform(xDif / 7 , yDif / 7);
            repaint();
        }
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
        mouseCurrentDragPoint = e.getPoint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2) mouseStartDragPoint = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseStartDragPoint = null;
        mouseCurrentDragPoint = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
