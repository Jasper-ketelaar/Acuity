package com.acuitybotting.path_finding.debugging.interactive_map;

import com.acuitybotting.path_finding.debugging.interactive_map.ui.MapFrame;

public class Test {

    public static void main(String[] args) {
        try {
            new MapFrame().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
