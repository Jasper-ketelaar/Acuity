package com.acuitybotting.data.flow.messaging.services.identity;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
public class RoutingUtil {

    public static String routeToUserId(String route){
        int start = route.indexOf("user.") + "user.".length();
        return route.substring(start, route.indexOf(".", start));
    }
}
