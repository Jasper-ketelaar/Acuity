package com.acuitybotting.security.acuity.web;

import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by Zachary Herridge on 6/7/2018.
 */
public class AcuityWebSecurity {

    public static AcuityPrincipal getPrincipal(){
        return (AcuityPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
