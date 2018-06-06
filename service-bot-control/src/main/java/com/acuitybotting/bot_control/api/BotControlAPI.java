package com.acuitybotting.bot_control.api;

import com.acuitybotting.aws.security.cognito.CognitoAuthenticationService;
import com.acuitybotting.bot_control.services.managment.BotControlManagementService;
import com.acuitybotting.db.arango.bot_control.domain.BotInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@RestController
@RequestMapping("/api/bot/control")
public class BotControlAPI {

    private final BotControlManagementService managementService;
    private final CognitoAuthenticationService cognitoAuthenticationService;

    @Autowired
    public BotControlAPI(BotControlManagementService managementService, CognitoAuthenticationService cognitoAuthenticationService) {
        this.managementService = managementService;
        this.cognitoAuthenticationService = cognitoAuthenticationService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public BotInstance registerInstance(){
        return managementService.register();
    }

    @RequestMapping(value = "/heartbeat", method = RequestMethod.POST)
    private boolean heartbeat(@RequestBody String id){
        return managementService.heartbeat(id);
    }
}
