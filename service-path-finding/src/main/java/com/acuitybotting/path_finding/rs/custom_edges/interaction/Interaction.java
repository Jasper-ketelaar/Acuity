package com.acuitybotting.path_finding.rs.custom_edges.interaction;

import com.google.gson.annotations.Expose;

import java.util.Map;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
public class Interaction {

    public static final int NPC = 1;
    public static final int SCENE_ENTITY = 2;
    public static final int SPELL = 3;
    public static final int INTERFACE = 4;
    public static final int FAIRY_RING = 4;

    @Expose
    private int type;

    @Expose
    private Map<String, Object> interactionData;

    public Interaction withData(String key, Object value){
        interactionData.put(key, value);
        return this;
    }

    public Interaction setType(int type) {
        this.type = type;
        return this;
    }
}
