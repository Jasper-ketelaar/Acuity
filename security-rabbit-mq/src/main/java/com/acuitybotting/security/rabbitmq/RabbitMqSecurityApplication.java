package com.acuitybotting.security.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by Zachary Herridge on 7/10/2018.
 */
@SpringBootApplication()
@ComponentScan("com.acuitybotting")
@PropertySource("classpath:rabbit.credentials")
public class RabbitMqSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqSecurityApplication.class, args);
    }
}
