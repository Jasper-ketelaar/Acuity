package com.acuitybotting.path_finding;

import com.acuitybotting.db.arango.ArangoDBConfigAcuity;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Created by Zachary Herridge on 5/31/2018.
 */
@SpringBootApplication()
@ComponentScan(value = "com.acuitybotting", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ArangoDBConfigAcuity.class))
public class PathFindingApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(PathFindingApplication.class);
        builder.headless(false);
        builder.run(args);
    }
}
