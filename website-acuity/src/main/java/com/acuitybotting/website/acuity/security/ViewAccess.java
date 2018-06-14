package com.acuitybotting.website.acuity.security;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Zachary Herridge on 6/14/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ViewAccess {
    String value();
}
