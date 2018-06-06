package com.acuitybotting.bot_control.api;

import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import com.acuitybotting.bot_control.services.managment.BotControlManagementService;
import com.acuitybotting.db.arango.bot_control.domain.BotInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@RestController
@RequestMapping("/api/bot/control")
public class BotControlAPI {

    private final BotControlManagementService managementService;
    private final AcuityJwtService jwtService;

    @Autowired
    public BotControlAPI(BotControlManagementService managementService, AcuityJwtService jwtService) {
        this.managementService = managementService;
        this.jwtService = jwtService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public BotInstance registerInstance(@RequestHeader String authToken){
        return managementService.register();
    }

    @RequestMapping(value = "/request-queue", method = RequestMethod.POST)
    public String registerQueue(HttpServletRequest request){
        return managementService.requestMessagingQueue(request.getRemoteAddr()).getQueueUrl();
    }

    @RequestMapping(value = "/heartbeat", method = RequestMethod.POST)
    private boolean heartbeat(@RequestBody String id){
        return managementService.heartbeat(id);
    }
}
