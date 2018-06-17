package com.acuitybotting.path_finding.debugging.interactive_map.plugin;

import com.acuitybotting.path_finding.debugging.interactive_map.util.GameMap;
import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapPanel;
import com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective;
import lombok.Getter;

@Getter
public abstract class Plugin implements PaintListener{

    private final MapPanel mapPanel;

    public Plugin(final MapPanel mapPanel) {
        this.mapPanel = mapPanel;
    }

    public GameMap getGameMap(){
        return this.getMapPanel().getGameMap();
    }

    public Perspective getPerspective(){
        return this.getMapPanel().getPerspective();
    }

    public abstract void onLoad();

    public abstract void onClose();
}
