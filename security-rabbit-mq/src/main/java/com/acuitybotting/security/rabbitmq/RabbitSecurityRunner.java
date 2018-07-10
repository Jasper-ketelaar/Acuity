package com.acuitybotting.security.rabbitmq;

import com.acuitybotting.security.rabbitmq.service.RabbitSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by Zachary Herridge on 7/10/2018.
 */
@Component
public class RabbitSecurityRunner implements CommandLineRunner {

    private RabbitSecurityService rabbitSecurityService;

    @Autowired
    public RabbitSecurityRunner(RabbitSecurityService rabbitSecurityService) {
        this.rabbitSecurityService = rabbitSecurityService;
    }

    @Override
    public void run(String... strings) throws Exception {
        try {
            rabbitSecurityService.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
