package com.acuitybotting.website.acuity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@ComponentScan("com.acuitybotting")
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class DashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class);
    }
}
