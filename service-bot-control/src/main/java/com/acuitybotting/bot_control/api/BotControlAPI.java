package com.acuitybotting.bot_control.api;

import com.acuitybotting.bot_control.services.managment.BotControlManagementService;
import com.acuitybotting.db.arango.acuity.bot_control.domain.BotInstance;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import com.acuitybotting.security.acuity.spring.AcuityPrincipalContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PreAuthorize("hasAuthority('BASIC_USER')")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity registerInstance(HttpServletRequest request){
        AcuityPrincipal acuityPrincipal = AcuityPrincipalContext.getPrincipal().orElse(null);
        if (acuityPrincipal == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        try {
            return new ResponseEntity<>(managementService.register(acuityPrincipal, request.getRemoteAddr()), HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
