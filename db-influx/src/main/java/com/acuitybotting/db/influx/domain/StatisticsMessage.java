package com.acuitybotting.db.influx.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class StatisticsMessage {

    private String type;
    private Map<String, Long> values;
    private Map<String, String> tags;
    private long creationTime;

}