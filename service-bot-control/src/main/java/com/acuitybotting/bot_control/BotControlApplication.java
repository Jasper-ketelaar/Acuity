package com.acuitybotting.bot_control;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@SpringBootApplication()
@ComponentScan("com.acuitybotting")
public class BotControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotControlApplication.class, args);
        new BotControlApplication();
    }
}
