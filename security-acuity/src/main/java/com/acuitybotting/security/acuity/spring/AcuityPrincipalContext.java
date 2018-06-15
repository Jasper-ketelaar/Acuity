package com.acuitybotting.security.acuity.spring;

import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/7/2018.
 */
public class AcuityPrincipalContext {

    public static Optional<AcuityPrincipal> getPrincipal(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AcuityPrincipal)) return Optional.empty();
        return Optional.of((AcuityPrincipal) authentication.getPrincipal());
    }

    public static String getPrincipalKey(){
        AcuityPrincipal principal = getPrincipal().orElse(null);
        if (principal != null) return principal.getKey();
        return null;
    }

    public static boolean hasRole(String owner) {
        return getPrincipal().map(acuityPrincipal -> acuityPrincipal.getRoles() != null && Arrays.stream(acuityPrincipal.getRoles()).anyMatch(s -> s.equalsIgnoreCase(owner))).orElse(false);
    }
}
