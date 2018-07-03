package com.acuitybotting.bot_control.api;

import com.acuitybotting.bot_control.services.managment.BotControlManagementService;
import com.acuitybotting.db.arango.acuity.bot_control.domain.BotInstance;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import com.acuitybotting.security.acuity.spring.AcuityPrincipalContext;
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
        AcuityPrincipal acuityPrincipal = AcuityPrincipalContext.getPrincipal().orElse(null);
        if (acuityPrincipal == null) throw new RuntimeException("Acuity principal is null");
        BotInstance register = managementService.register(acuityPrincipal, request.getRemoteAddr());
        if (register == null) throw new RuntimeException("Failed to register bot instance. " + acuityPrincipal + ", " + request.getRemoteAddr());
        return register;
    }
}
