package com.acuitybotting.path_finding.algorithms.hpa;

import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraphBuilder;
import lombok.Getter;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/18/2018.
 */
@Getter
@Service
public class HpaService {

    private HPAGraphBuilder HPAGraphBuilder = new HPAGraphBuilder();

    public HpaService setDimensions(int width, int height){
        HPAGraphBuilder.setRegionWidth(width).setRegionHeight(height);
        return this;
    }
}
