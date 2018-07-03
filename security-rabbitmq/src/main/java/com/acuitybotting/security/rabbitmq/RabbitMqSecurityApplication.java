package com.acuitybotting.security.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication()
@ComponentScan("com.acuitybotting")
public class RabbitMqSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqSecurityApplication.class, args);
    }
}
