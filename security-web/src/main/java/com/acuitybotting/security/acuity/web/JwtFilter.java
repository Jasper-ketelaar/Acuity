package com.acuitybotting.security.acuity.web;

import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */
@Component
@Order(1)
public class JwtFilter implements Filter {

    private final AcuityJwtService jwtService;

    @Autowired
    public JwtFilter(AcuityJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorization = ((HttpServletRequest) request).getHeader("Authorization");
        DecodedJWT jwt = jwtService.decodeAndVerify(authorization).orElse(null);
        if (jwt != null){
            AcuityPrincipal acuityPrincipal = new AcuityPrincipal();
            acuityPrincipal.setUsername(jwt.getClaim("cognito:username").asString());
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(acuityPrincipal, null, new HashSet<>()));
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
