package com.acuitybotting.website.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */
@ComponentScan("com.acuitybotting")
@SpringBootApplication
public class DashboardApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
    }
}
