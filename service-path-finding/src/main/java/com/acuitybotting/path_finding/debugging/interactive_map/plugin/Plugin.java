package com.acuitybotting.path_finding.debugging.interactive_map.plugin;

import com.acuitybotting.path_finding.debugging.interactive_map.util.GameMap;
import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapRenderer;
import com.acuitybotting.path_finding.debugging.interactive_map.util.Perspective;
import lombok.Getter;

@Getter
public abstract class Plugin implements PaintListener{

    private final MapRenderer mapRenderer;

    public Plugin(final MapRenderer mapRenderer) {
        this.mapRenderer = mapRenderer;
    }

    public GameMap getGameMap(){
        return getMapRenderer().getGameMap();
    }

    public Perspective getPerspective(){
        return getMapRenderer().getPerspective();
    }

    public abstract void onLoad();

    public abstract void onClose();
}
