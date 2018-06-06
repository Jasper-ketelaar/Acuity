package com.acuitybotting.aws.security.web_api;

import com.acuitybotting.aws.security.cognito.CognitoJwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class JWTWebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final CognitoJwtService cognitoJwtService;

  @Autowired
  public JWTWebSecurityConfig(CognitoJwtService cognitoJwtService) {
    this.cognitoJwtService = cognitoJwtService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().authorizeRequests()
        .anyRequest().authenticated()
        .and().addFilterBefore(new JWTAuthenticationFilter(cognitoJwtService), UsernamePasswordAuthenticationFilter.class);
  }
}