package com.acuitybotting.db.influx;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */

@Service
public class InfluxDbService {

    private InfluxDB influxDB;

    public InfluxDB connect(String host, String username, String password){
        influxDB = InfluxDBFactory.connect(host, username, password);
        return influxDB;
    }
}
