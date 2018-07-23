package com.acuitybotting.bot_control.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RabbitDbRequest {

    public static final int SAVE = 0;
    public static final int FIND_BY_KEY = 1;
    public static final int FIND_BY_GROUP = 2;
    public static final int DELETE_BY_KEY = 3;

    private Integer type;

    private String database;
    private String id;
    private String group;
    private String key;
    private String rev;

    private String document;
    private String documentQuery;
}