package com.acuitybotting.bot.statistics.api;

import com.acuitybotting.bot.statistics.domain.ExpGained;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */
@RestController
@RequestMapping("/api/bot/statistics/productivity")
public class ProductivityAPI {

    private final InfluxDBTemplate<Point> influxDBTemplate;

    @Autowired
    public ProductivityAPI(InfluxDBTemplate<Point> influxDBTemplate) {
        this.influxDBTemplate = influxDBTemplate;
    }

    @RequestMapping(value = "/exp-gained", method = RequestMethod.POST)
    public void expGained(@RequestBody ExpGained[] gains) {
        List<Point> points = new ArrayList<>();
        for (ExpGained expGained : gains) {
            Point build = Point.measurement("productivity-xp-gained")
                    .addField("skill", expGained.getSkill())
                    .addField("change", expGained.getXpAmount())
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .build();
            points.add(build);
        }
        influxDBTemplate.write(points);
    }
}
