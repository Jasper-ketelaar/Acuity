package com.acuitybotting.path_finding.debugging.interactive_map.ui;

import com.acuitybotting.path_finding.debugging.interactive_map.util.GameMap;
import com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.debugging.interactive_map.plugin.impl.PositionPlugin;
import com.acuitybotting.path_finding.debugging.interactive_map.util.ScreenLocation;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
public class MapPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener {

    private GameMap gameMap;
    private Perspective perspective;

    private List<Plugin> plugins = new ArrayList<>();

    private Point lastMousePosition = null;
    private Point mouseStartDragPoint = null;
    private Point mouseCurrentDragPoint = null;

    public MapPanel(GameMap gameMap) {
        this.gameMap = gameMap;
        this.perspective =  new Perspective(gameMap, this);

        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);

        addPlugin(new PositionPlugin());

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::handleDrag, 100, 120, TimeUnit.MILLISECONDS);
    }

    public void addPlugin(Plugin plugin){
        plugin.attach(this);
        plugins.add(0, plugin);
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

        g2.scale(perspective.getScale(), perspective.getScale());
        ScreenLocation point = perspective.locationToMap(perspective.getBase());
        g2.drawImage(this.gameMap.getMapImage(), -Perspective.round(point.getX()), -Perspective.round(point.getY()), this);

        for (Plugin plugin : plugins) {
            plugin.onPaint(g1, g2);
        }
    }

    public Location getMouseLocation(){
        return perspective.screenToLocation(getLastMousePosition());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastMousePosition = e.getPoint();
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
    public void mouseWheelMoved(MouseWheelEvent e) {
        Location location = perspective.getCenterLocation();
        boolean zoom = e.getWheelRotation() > 0;
        perspective.incScale(zoom ? -.2 : .2);
        perspective.centerOn(location);
        repaint();
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
