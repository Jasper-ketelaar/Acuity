package com.acuitybotting.website.acuity.views.login;

import com.acuitybotting.security.acuity.aws.cognito.CognitoAuthenticationService;
import com.acuitybotting.security.acuity.aws.cognito.domain.CognitoConfiguration;
import com.acuitybotting.security.acuity.aws.cognito.domain.CognitoTokens;
import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@UIScope
@SpringView(name = "Login")
public class LoginView extends MVerticalLayout implements View {

    private final CognitoAuthenticationService authService;

    private final AcuityJwtService acuityJwtService;

    @Autowired
    public LoginView(CognitoAuthenticationService authService, AcuityJwtService acuityJwtService) {
        this.authService = authService;
        this.acuityJwtService = acuityJwtService;
    }

    @PostConstruct
    public void init(){
        authService.setCognitoConfiguration(
                CognitoConfiguration.builder()
                        .poolId("us-east-1_HrbYmVhlY")
                        .clientAppId("3pgbd576sg70tsub4nh511k58u")
                        .fedPoolId("us-east-1:ff1b33f4-7f66-47a5-b7ff-9696b0e1fb52")
                        .customDomain("acuitybotting")
                        .region("us-east-1")
                        .redirectUrl("https://rspeer.org/")
                        .build()
        );

        MTextField username = new MTextField("Username");
        PasswordField password = new PasswordField("Password");
        MLabel loginMessage = new MLabel("").withVisible(false);
        MButton login = new MButton("Login").withStyleName(ValoTheme.BUTTON_PRIMARY);
        with(username, password, loginMessage, login);

        login.addClickListener(clickEvent -> {
            CognitoTokens cognitoTokens = authService.login(username.getValue(), password.getValue()).orElse(null);
            if (cognitoTokens == null) loginMessage.withValue("Failed to login.").withVisible(true);
            else {
                AcuityPrincipal acuityPrincipal = acuityJwtService.getPrincipal(cognitoTokens.getIdToken()).orElse(null);
                if (acuityPrincipal == null) loginMessage.withValue("Failed to login.").withVisible(true);
                else {
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    if (acuityPrincipal.getRoles() != null) for (String role : acuityPrincipal.getRoles()) authorities.add(new SimpleGrantedAuthority(role));
                    authorities.add(new SimpleGrantedAuthority("BASIC_USER"));
                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(acuityPrincipal, null, authorities));
                    loginMessage.withValue("Logged in as: " + acuityPrincipal.getUsername()).withVisible(true);
                }
            }
        });
    }
}
