package com.acuitybotting.path_finding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Zachary Herridge on 5/31/2018.
 */
@SpringBootApplication()
@ComponentScan("com.acuitybotting")
public class PathFindingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PathFindingApplication.class, args);
    }
}
