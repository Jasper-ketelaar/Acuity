package com.acuitybotting.bot.statistics;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@ComponentScan("com.acuitybotting")
public class BotStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotStatisticsApplication.class, args);
    }
}
