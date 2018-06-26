package com.acuitybotting.path_finding.debugging.interactive_map.plugin;

import com.acuitybotting.path_finding.debugging.interactive_map.ui.PaintUtil;
import com.acuitybotting.path_finding.debugging.interactive_map.util.GameMap;
import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapPanel;
import com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective;
import lombok.Getter;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@Getter
public abstract class Plugin implements PaintListener, MouseListener{

    private MapPanel mapPanel;

    public Perspective getPerspective(){
        return this.getMapPanel().getPerspective();
    }

    public Plugin setMapPanel(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
        return this;
    }

    public void attach(MapPanel mapPanel){
        this.mapPanel = mapPanel;
        mapPanel.addMouseListener(this);
    }

    public PaintUtil getPaintUtil(){
        return getMapPanel().getPaintUtil();
    }

    public void detach(){
        this.mapPanel.removeMouseListener(this);
    }

    public abstract void onLoad();

    public abstract void onClose();

    @Override
    public void mouseClicked(MouseEvent e){}

    @Override
    public void mousePressed(MouseEvent e){}

    @Override
    public void mouseReleased(MouseEvent e){}

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e){}
}
