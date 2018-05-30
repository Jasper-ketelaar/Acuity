package com.acuitybotting.db.arango;

import com.acuitybotting.db.arango.entities.TileFlagData;
import com.acuitybotting.db.arango.repositories.TileFlagRepository;
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

    @Override
    public void run(String... strings) throws Exception {


        Iterable<TileFlagData> byLocationWithinAndPlane = tileFlagRepository.findByLocationWithinAndPlane(new Polygon(new Point(0, 0), new Point(0, 100), new Point(100, 100), new Point(100, 0)), 1);
        for (TileFlagData tileFlagData : byLocationWithinAndPlane) {
            System.out.println(tileFlagData);
        }

    }

}
