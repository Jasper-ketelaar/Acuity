package com.acuitybotting.db.arango;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AcutiyArangoDBApplication {

    public static void main(String[] args) {
        Class[] runner = new Class[]{Runner.class};
        SpringApplication.run(runner, args);
    }
}
