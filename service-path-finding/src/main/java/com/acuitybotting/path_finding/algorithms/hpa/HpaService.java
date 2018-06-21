package com.acuitybotting.path_finding.algorithms.hpa;

import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import lombok.Getter;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/18/2018.
 */
@Getter
@Service
public class HpaService {

    private HPAGraph graph = new HPAGraph();
}
