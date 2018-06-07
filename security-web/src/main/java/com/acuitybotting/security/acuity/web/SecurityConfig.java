package com.acuitybotting.security.acuity.web;

import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


@Configuration
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AcuityJwtService acuityJwtService;

    @Autowired
    public SecurityConfig(AcuityJwtService acuityJwtService) {
        this.acuityJwtService = acuityJwtService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("STATELESS");
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests().anyRequest().permitAll();

        http.apply(new JwtTokenFilterConfigurer(acuityJwtService));
    }
}