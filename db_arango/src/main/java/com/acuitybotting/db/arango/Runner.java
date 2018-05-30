package com.acuitybotting.db.arango;

import com.acuitybotting.db.arango.entities.TileFlagData;
import com.acuitybotting.db.arango.repositories.TileFlagRepository;
import com.arangodb.springframework.core.ArangoOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;

/**
 * Created by Zachary Herridge on 5/30/2018.
 */
@ComponentScan("com.acuitybotting.db.arango")
public class Runner implements CommandLineRunner {

    @Autowired
    private TileFlagRepository tileFlagRepository;

    @Autowired
    private ArangoOperations arangoOperations;

    @Override
    public void run(String... strings) throws Exception {
        System.out.println(arangoOperations.getVersion().getVersion());
    }
}
