package com.acuitybotting.db.arango;

import com.acuitybotting.db.arango.repositories.TileFlagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
@ComponentScan("com.acuitybotting.db.arango")
public class Runner implements CommandLineRunner {

    @Autowired
    private TileFlagRepository tileFlagRepository;

    @Override
    public void run(String... strings) throws Exception {
        System.out.println(tileFlagRepository.count());
    }
}
