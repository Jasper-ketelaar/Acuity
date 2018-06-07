package com.acuitybotting.security.acuity.web;

import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtTokenFilter extends GenericFilterBean {

    private final AcuityJwtService acuityJwtService;

    public JwtTokenFilter(AcuityJwtService acuityJwtService) {
        this.acuityJwtService = acuityJwtService;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication((HttpServletRequest) req));
        filterChain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        DecodedJWT jwt = acuityJwtService.decodeAndVerify(authorization).orElse(null);
        if (jwt != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("USER"));
            AcuityPrincipal acuityPrincipal = new AcuityPrincipal();
            acuityPrincipal.setUsername(jwt.getClaim("cognito:username").asString());
            return new UsernamePasswordAuthenticationToken(acuityPrincipal, null, authorities);
        }
        return null;
    }
}