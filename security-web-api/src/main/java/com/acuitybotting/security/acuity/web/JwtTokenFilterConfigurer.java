package com.acuitybotting.security.acuity.web;

import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

  private final AcuityJwtService acuityJwtService;

  public JwtTokenFilterConfigurer(AcuityJwtService acuityJwtService) {
    this.acuityJwtService = acuityJwtService;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    JwtTokenFilter customFilter = new JwtTokenFilter(acuityJwtService);
    http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
  }
}