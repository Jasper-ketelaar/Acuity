package com.acuitybotting.bot.statistics.api;

import com.acuitybotting.bot.statistics.domain.LogScriptRuntime;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */
@RestController
@RequestMapping("/api/bot/statistics/script")
public class ScriptAPI {

    private final InfluxDBTemplate<Point> influxDBTemplate;

    @Autowired
    public ScriptAPI(InfluxDBTemplate<Point> influxDBTemplate) {
        this.influxDBTemplate = influxDBTemplate;
    }


    @RequestMapping(value = "/log-runtime", method = RequestMethod.POST)
    public void runtime(@RequestBody LogScriptRuntime scriptRuntime){
        Point build = Point.measurement("script-runtime")
                .addField("name", scriptRuntime.getScriptName())
                .addField("author", scriptRuntime.getScriptAuthor())
                .addField("runtime", scriptRuntime.getTime())
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();
    }
}
