package com.acuitybotting.path_finding.algorithms.hpa;

import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.GraphBuilder;
import lombok.Getter;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/18/2018.
 */
@Getter
@Service
public class HpaService {

    private GraphBuilder graphBuilder = new GraphBuilder();

    public HpaService setDimensions(int width, int height){
        graphBuilder.setRegionWidth(width).setRegionHeight(height);
        return this;
    }
}
