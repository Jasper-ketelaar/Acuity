package com.acuitybotting.security.acuity.web;

import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by Zachary Herridge on 6/7/2018.
 */
public class AcuityWebSecurity {

    public static AcuityPrincipal getPrincipal(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AcuityPrincipal)) return null;
        return (AcuityPrincipal) authentication.getPrincipal();
    }

    public static String getPrincipalKey(){
        AcuityPrincipal principal = getPrincipal();
        if (principal != null) return principal.getKey();
        return null;
    }

}
