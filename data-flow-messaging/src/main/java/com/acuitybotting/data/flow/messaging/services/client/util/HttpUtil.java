package com.acuitybotting.data.flow.messaging.services.client.util;

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

    private static final String USER_AGENT = "Mozilla/5.0";

    public static String get(String accessKey, String accessSecret, String url, TreeMap<String, String> queryParams) throws Exception {
        Map<String, String> headers = Collections.emptyMap();
        if (accessKey != null && accessSecret != null){
            TreeMap<String, String> awsHeaders = new TreeMap<>();
            awsHeaders.put("host", "sqs.us-east-1.amazonaws.com");
            headers = new AWSV4Auth.Builder(accessKey, accessSecret)
                    .regionName("us-east-1")
                    .serviceName("sqs")
                    .httpMethodName("GET")
                    .canonicalURI("/604080725100/test.fifo")
                    .queryParametes(queryParams)
                    .awsHeaders(awsHeaders)
                    .debug()
                    .build()
                    .getHeaders();
        }

        url += "?" + queryParams.entrySet().stream().map(entry -> entry.getKey() + "=" + encode(entry.getValue())).collect(Collectors.joining("&"));

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
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

    public static String encode(Object param) {
        try {
            return URLEncoder.encode(String.valueOf(param), "UTF-8");
        } catch (Exception e) {
            return URLEncoder.encode(String.valueOf(param));
        }
    }
}
