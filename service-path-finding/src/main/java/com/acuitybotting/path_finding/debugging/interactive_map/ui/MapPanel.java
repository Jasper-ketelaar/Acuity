package com.acuitybotting.path_finding.debugging.interactive_map.ui;

import com.acuitybotting.path_finding.debugging.interactive_map.plugin.Plugin;
import com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective;
import com.acuitybotting.path_finding.debugging.interactive_map.util.ScreenLocation;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import com.acuitybotting.path_finding.rs.utils.RsMapService;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
public class MapPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {

    private Perspective perspective;

    private List<Plugin> plugins = new ArrayList<>();

    private Point lastMousePosition = null;
    private Point mouseStartDragPoint = null;
    private Point mouseCurrentDragPoint = null;

    private PaintUtil paintUtil = new PaintUtil(this);

    public MapPanel() {
        this.perspective = new Perspective(this);

        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::handleDrag, 100, 120, TimeUnit.MILLISECONDS);
    }

    public void addPlugin(Plugin plugin) {
        plugin.attach(this);
        plugins.add(0, plugin);
    }

    private void handleDrag() {
        if (mouseStartDragPoint != null && mouseCurrentDragPoint != null) {
            int xDif = mouseCurrentDragPoint.x - mouseStartDragPoint.x;
            int yDif = mouseStartDragPoint.y - mouseCurrentDragPoint.y;
            perspective.getBase().transform(xDif / 7, yDif / 7);
            repaint();
        }
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        Graphics2D g1 = (Graphics2D) g.create();
        paintUtil.onPaintStart(g1);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.scale(perspective.getScale(), perspective.getScale());


        int xSteps = Perspective.round(perspective.getTileWidth() / 64) + 2;
        int ySteps = Perspective.round(perspective.getTileHeight() / 64) + 2;

        Location regionBase = RsMapService.locationToRegionBase(perspective.screenToLocation(new Point(0, 0)));

        for (int x = -2; x < xSteps; x++) {
            for (int y = -2; y < ySteps; y++) {
                Location clone = regionBase.clone(x * 64, -(y * 64));
                BufferedImage regionImage = RsEnvironment.getRegionImage(clone, perspective.getBase().getPlane());
                if (regionImage != null) {
                    Location off = clone.clone(0, 64);
                    ScreenLocation point = perspective.locationToMap(off);
                    g2.drawImage(regionImage, Perspective.round(point.getX()), Perspective.round(point.getY()), null);
                }
            }
        }


        for (Plugin plugin : plugins) {
            plugin.onPaint(g1);
        }

        paintUtil.onPaintEnd(g1);
    }

    public Location getMouseLocation() {
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
    public void keyTyped(KeyEvent e) {

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

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
            perspective.getBase().setPlane(perspective.getBase().getPlane() + 1);
        }
        if (e.getKeyCode() == KeyEvent.VK_MINUS) {
            perspective.getBase().setPlane(perspective.getBase().getPlane() - 1);
        }
        if (e.getKeyCode() == KeyEvent.VK_0) {
            File regionImageBase = RsEnvironment.getRegionImageBase();
            if (regionImageBase.getName().contains("a_")){
                RsEnvironment.setRegionImageBase(new File("C:\\Users\\zgher\\Desktop\\Map Info\\img\\regions"));
            }
            else {
                RsEnvironment.setRegionImageBase(new File("C:\\Users\\zgher\\Desktop\\Map Info\\img\\a_regions"));
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
