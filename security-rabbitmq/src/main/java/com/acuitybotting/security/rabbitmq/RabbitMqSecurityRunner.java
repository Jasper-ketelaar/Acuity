package com.acuitybotting.security.rabbitmq;

import com.acuitybotting.security.rabbitmq.auth.RabbitMqAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqSecurityRunner implements CommandLineRunner {

    private final RabbitMqAuthService authService;

    @Autowired
    public RabbitMqSecurityRunner(RabbitMqAuthService authService) {
        this.authService = authService;
    }

    @Override
    public void run(String... args) throws Exception {
        authService.start();
    }
}
