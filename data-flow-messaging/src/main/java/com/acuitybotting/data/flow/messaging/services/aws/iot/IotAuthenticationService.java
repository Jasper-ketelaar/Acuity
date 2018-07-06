package com.acuitybotting.data.flow.messaging.services.aws.iot;

import com.acuitybotting.data.flow.messaging.services.aws.iot.domain.RegisterResponse;
import com.acuitybotting.data.flow.messaging.services.utils.HttpUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Zachary Herridge on 7/6/2018.
 */
@Slf4j
public class IotAuthenticationService {

    public static Optional<RegisterResponse> authenticate(String jwt){
        try {
            Gson gson = new Gson();

            String authUrl = "https://7ja4dnkku1.execute-api.us-east-1.amazonaws.com/Prod/registerIotConnection";

            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");

            String body = gson.toJson(Collections.singletonMap("token", jwt));
            String response = HttpUtil.makeRequest("POST", headers, authUrl, null, body);
            RegisterResponse registerResponse = gson.fromJson(response, RegisterResponse.class);

            return Optional.ofNullable(registerResponse);
        } catch (Exception e) {
            log.error("Error authenticating iot.", e);
        }
        return Optional.empty();
    }

}
