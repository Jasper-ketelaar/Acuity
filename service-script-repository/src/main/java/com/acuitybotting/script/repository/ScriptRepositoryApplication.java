package com.acuitybotting.script.repository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Zachary Herridge on 6/12/2018.
 */
@SpringBootApplication()
@ComponentScan("com.acuitybotting")
public class ScriptRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScriptRepositoryApplication.class, args);
    }
}
