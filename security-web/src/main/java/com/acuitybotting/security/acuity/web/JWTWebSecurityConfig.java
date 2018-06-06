/*
package com.acuitybotting.aws.security.web_api;

import com.acuitybotting.aws.security.jwt.AcuityJwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class JWTWebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final AcuityJwtService acuityJwtService;

  @Autowired
  public JWTWebSecurityConfig(AcuityJwtService acuityJwtService) {
    this.acuityJwtService = acuityJwtService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .anyRequest().authenticated()
        .and().addFilterBefore(new JWTAuthenticationFilter(acuityJwtService), UsernamePasswordAuthenticationFilter.class);
  }
}*/
