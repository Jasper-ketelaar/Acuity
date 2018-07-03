package com.acuitybotting.data.flow.messaging.services.sqs.client.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 6/19/2018.
 */
public class HttpUtil {

    public static String get(Map<String, String> headers, String url, TreeMap<String, String> queryParams) throws Exception {
        if (queryParams != null) url += "?" + queryParams.entrySet().stream().map(entry -> entry.getKey() + "=" + encode(entry.getValue())).collect(Collectors.joining("&"));

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        if (headers != null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))){
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        return response.toString();
    }

    private static String encode(Object param) {
        try {
            return URLEncoder.encode(String.valueOf(param), "UTF-8");
        } catch (Exception e) {
            return URLEncoder.encode(String.valueOf(param));
        }
    }
}
