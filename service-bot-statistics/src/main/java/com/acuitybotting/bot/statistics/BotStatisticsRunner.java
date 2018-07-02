package com.acuitybotting.bot.statistics;

import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */
@Component
public class BotStatisticsRunner implements CommandLineRunner{

    private final InfluxDBTemplate<Point> influxDBTemplate;

    @Autowired
    public BotStatisticsRunner(InfluxDBTemplate<Point> influxDBTemplate) {
        this.influxDBTemplate = influxDBTemplate;
    }

    @Override
    public void run(String... strings) throws Exception {
        influxDBTemplate.createDatabase();

        Point build = Point.measurement("testmes")
                .addField("value", 10).build();

        influxDBTemplate.write(build);
    }
}
