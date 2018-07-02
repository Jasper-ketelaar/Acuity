package com.acuitybotting.bot.statistics.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */
@RestController
public class InfluxDbAuthMiddleman {

    @Value("${influx.username}")
    private String influxUsername;

    @Value("${influx.password}")
    private String influxPassword;

    @RequestMapping(value = "/*", method = RequestMethod.POST)
    public ResponseEntity<String> post(HttpServletRequest request, @RequestParam("u") String username, @RequestParam("p") String password) throws IOException {
        return handle(request, username, password);
    }

    @RequestMapping(value = "/*", method = RequestMethod.GET)
    public ResponseEntity<String> get(HttpServletRequest request, @RequestParam("u") String username, @RequestParam("p") String password) throws IOException {
        return handle(request, username, password);
    }

    private ResponseEntity<String> handle(HttpServletRequest request, String username, String password) throws IOException {
        if (!influxUsername.equals(username) || !influxPassword.equals(password)){
            return new ResponseEntity<>("Acuity auth failed", HttpStatus.UNAUTHORIZED);
        }

        String url = "http://localhost:8086" + request.getServletPath() + "?" + request.getQueryString();

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod(request.getMethod());

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder responseString = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            responseString.append(inputLine);
        }
        in.close();

        return new ResponseEntity<>(responseString.toString(), HttpStatus.valueOf(responseCode));
    }
}
