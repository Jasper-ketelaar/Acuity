package com.acuitybotting.bot.statistics.domain;

import lombok.Data;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */
@Data
public class LogScriptRuntime {

    private String scriptName;
    private String scriptAuthor;
    private long time;

}
