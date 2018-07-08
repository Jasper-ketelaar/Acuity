package com.acuitybotting.bot_control;

import com.acuitybotting.bot_control.services.managment.BotControlManagementService;
import com.acuitybotting.data.flow.messaging.services.aws.iot.IotClientService;
import com.acuitybotting.data.flow.messaging.services.aws.iot.client.IotClientListener;
import com.acuitybotting.db.influx.InfluxDbService;
import com.acuitybotting.db.influx.domain.StatisticsMessage;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@Component
@Slf4j
@PropertySource("classpath:iot.credentials")
public class BotControlRunner implements CommandLineRunner{

    @Value("${rspeer.token}")
    private String token;

    @Value("${iot.access}")
    private String access;

    @Value("${iot.secret}")
    private String secret;

    private final InfluxDbService influxDbService;
    private final BotControlManagementService managementService;

    @Autowired
    public BotControlRunner(InfluxDbService influxDbService, BotControlManagementService managementService) {
        this.influxDbService = influxDbService;
        this.managementService = managementService;
    }

    @Override
    public void run(String... strings) throws Exception {
        try {

            Gson gson = new Gson();
            InfluxDB influxDB = influxDbService.connect("http://68.46.70.47:8090", "root", "sW5cu$uhEx&p");
            influxDB.setDatabase("acuity.botting");

            String clientEndpoint = "a2i158467e5k2v.iot.us-east-1.amazonaws.com";
            String clientId = "stats-worker-1";

            IotClientService iotClientService = new IotClientService();

            iotClientService.auth(clientEndpoint, clientId, access, secret, null);
            iotClientService.getClient().getListeners().add(new IotClientListener() {
                @Override
                public void onConnect() {
                    try {
                        System.out.println("Connected");


           /*             iotClientService.consume("user/+/connection/+/services/statistics")
                                .withCallback(message -> {
                                    StatisticsMessage statisticsMessage = gson.fromJson(message.getBody(), StatisticsMessage.class);

                                    for (Map.Entry<String, Long> entry : statisticsMessage.getValues().entrySet()) {
                                        Point.Builder measurement = Point.measurement(statisticsMessage.getType());

                                        measurement.addField("value", entry.getValue());

                                        if (statisticsMessage.getType().equals("script-xp-changes")) measurement.tag("skill", entry.getKey().toLowerCase());
                                        for (Map.Entry<String, String> tag : statisticsMessage.getTags().entrySet()) {
                                            measurement.tag(tag.getKey(), tag.getValue());
                                        }
                                        influxDB.write(measurement.build());
                                    }


                                })
                                .start();*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionFailure() {

                }

                @Override
                public void onConnectionClosed() {

                }
            });

            iotClientService.connect();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
