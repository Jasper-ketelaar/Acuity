package com.acuitybotting.website.acuity.security;

import com.acuitybotting.security.acuity.aws.cognito.CognitoAuthenticationService;
import com.acuitybotting.security.acuity.aws.cognito.domain.CognitoConfiguration;
import com.acuitybotting.security.acuity.aws.cognito.domain.CognitoTokens;
import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zachary Herridge on 6/14/2018.
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends GlobalMethodSecurityConfiguration {

    private final CognitoAuthenticationService authService;
    private final AcuityJwtService acuityJwtService;

    static {
        SecurityContextHolder.setStrategyName(VaadinSessionSecurityContextHolderStrategy.class.getName());
    }

    @Autowired
    public SecurityConfiguration(CognitoAuthenticationService authService, AcuityJwtService acuityJwtService) {
        this.authService = authService;
        this.acuityJwtService = acuityJwtService;

        authService.setCognitoConfiguration(
                CognitoConfiguration.builder()
                        .poolId("us-east-1_aVWAQrVQG")
                        .clientAppId("46bktvte40p27r0mudbgcso6qn")
                        .region("us-east-1")
                        .build()
        );
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                CognitoTokens cognitoTokens = authService.login((String) authentication.getPrincipal(), (String) authentication.getCredentials()).orElse(null);
                if (cognitoTokens == null) throw new BadCredentialsException("Failed to login");
                else {
                    AcuityPrincipal acuityPrincipal = acuityJwtService.getPrincipal(cognitoTokens.getIdToken()).orElse(null);
                    if (acuityPrincipal == null) throw new BadCredentialsException("Failed to login");
                    else {
                        List<GrantedAuthority> authorities = new ArrayList<>();
                        if (acuityPrincipal.getRoles() != null) for (String role : acuityPrincipal.getRoles()) authorities.add(new SimpleGrantedAuthority(role));
                        authorities.add(new SimpleGrantedAuthority("BASIC_USER"));
                        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(acuityPrincipal, null, authorities));
                        return new UsernamePasswordAuthenticationToken(acuityPrincipal, null, authorities);
                    }
                }
            }

            @Override
            public boolean supports(Class<?> aClass) {
                return true;
            }
        });
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationManager();
    }
}