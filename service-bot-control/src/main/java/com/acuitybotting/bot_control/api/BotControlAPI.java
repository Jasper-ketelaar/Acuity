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
@RestController
@RequestMapping("/api/bot/control")
public class BotControlAPI {

    private final BotControlManagementService managementService;

    @Autowired
    public BotControlAPI(BotControlManagementService managementService) {
        this.managementService = managementService;
    }

    @PreAuthorize("authentication.principal.username == 'Zach'")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public BotInstance registerInstance(){
        AcuityPrincipal principal = AcuityWebSecurity.getPrincipal();
        return managementService.register(principal);
    }

    @RequestMapping(value = "/request-queue", method = RequestMethod.POST)
    public String registerQueue(@RequestBody String botAuthKey, HttpServletRequest request){
        return managementService.requestMessagingQueue(request.getRemoteAddr()).getQueueUrl();
    }

    @RequestMapping(value = "/heartbeat", method = RequestMethod.POST)
    private boolean heartbeat(@RequestBody String id){
        return managementService.heartbeat(id);
    }
}
