package com.acuitybotting.aws.security.web_api;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */

import com.acuitybotting.aws.security.cognito.CognitoJwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;

public class JWTAuthenticationFilter extends GenericFilterBean {

    private final CognitoJwtService cognitoJwtService;

    public JWTAuthenticationFilter(CognitoJwtService cognitoJwtService) {
        this.cognitoJwtService = cognitoJwtService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String idJWT = ((HttpServletRequest) request).getHeader("Authorization");
        UsernamePasswordAuthenticationToken authentication = cognitoJwtService.decodeAndVerify(idJWT)
                .map(decodedJWT -> new UsernamePasswordAuthenticationToken("asd", null, Collections.emptyList()))
                .orElse(null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request,response);
    }
}