package com.acuitybotting.bot_control.api;

import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import com.acuitybotting.bot_control.services.managment.BotControlManagementService;
import com.acuitybotting.db.arango.bot_control.domain.BotInstance;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import com.acuitybotting.security.acuity.web.AcuityWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Zachary Herridge on 6/1/2018.
 */
@PreAuthorize("hasAuthority('BASIC_USER')")
@RestController
@RequestMapping("/api/bot/control")
public class BotControlAPI {

    private final BotControlManagementService managementService;

    @Autowired
    public BotControlAPI(BotControlManagementService managementService) {
        this.managementService = managementService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public BotInstance registerInstance(HttpServletRequest request){
        AcuityPrincipal principal = AcuityWebSecurity.getPrincipal();
        BotInstance register = managementService.register(principal, request.getRemoteAddr());
        if (register == null) throw new RuntimeException("Failed to register bot instance. " + principal + ", " + request.getRemoteAddr());
        return register;
    }

    @RequestMapping(value = "/update-queue-policy", method = RequestMethod.POST)
    public boolean requestQueuePolicyUpdate(String authKey, HttpServletRequest request){
        return managementService.updateQueuePolicy(authKey, request.getRemoteAddr());
    }

    @RequestMapping(value = "/heartbeat", method = RequestMethod.POST)
    private boolean heartbeat(@RequestBody String id){
        return managementService.heartbeat(id);
    }
}
