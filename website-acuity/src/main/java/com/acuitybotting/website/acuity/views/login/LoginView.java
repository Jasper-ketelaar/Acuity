package com.acuitybotting.website.acuity.views.login;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityIdentityService;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import com.acuitybotting.security.acuity.spring.AcuitySecurityContext;
import com.acuitybotting.website.acuity.navigation.SpringNavigationService;
import com.acuitybotting.website.acuity.security.AcuityIdentityContext;
import com.acuitybotting.website.acuity.views.script_repository.views.ScriptListView;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@UIScope
@SpringView(name = "Login")
public class LoginView extends MVerticalLayout implements View {

    private final AcuityIdentityService identityService;
    private final AuthenticationManager authenticationManager;

    private MTextField username = new MTextField("Username");
    private MLabel loginMessage = new MLabel("").withVisible(false);
    private PasswordField password = new PasswordField("Password");

    @Autowired
    public LoginView(AcuityIdentityService identityService, AuthenticationManager authenticationManager) {
        this.identityService = identityService;
        this.authenticationManager = authenticationManager;
    }

    @PostConstruct
    public void init(){
        MButton login = new MButton("Login").withStyleName(ValoTheme.BUTTON_PRIMARY);
        with(username, password, loginMessage, login);
        login.addClickListener(this::login);
    }

    private void login(){
        try {
            loginMessage.withVisible(false);
            Authentication token = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username.getValue(), password.getValue()));
            SecurityContextHolder.getContext().setAuthentication(token);
            AcuityPrincipal acuityPrincipal = AcuitySecurityContext.getPrincipal().orElse(null);
            if (acuityPrincipal != null){
                AcuityIdentity acuityIdentity = identityService.createIfAbsent(acuityPrincipal);
                identityService.updateLoginFields(acuityIdentity);
                AcuityIdentityContext.getCurrent(false);
                SpringNavigationService.navigateTo(ScriptListView.class);
            }
        }
        catch (Exception e){
            loginMessage.withValue(e.getMessage()).withVisible(true);
            e.printStackTrace();
        }
    }
}
