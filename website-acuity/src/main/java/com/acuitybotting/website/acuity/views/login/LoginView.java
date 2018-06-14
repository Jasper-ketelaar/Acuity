package com.acuitybotting.website.acuity.views.login;

import com.vaadin.navigator.View;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    final
    AuthenticationManager authenticationManager;

    private MTextField username = new MTextField("Username");
    private PasswordField password = new PasswordField("Password");

    @Autowired
    public LoginView(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostConstruct
    public void init(){
        MLabel loginMessage = new MLabel("").withVisible(false);
        MButton login = new MButton("Login").withStyleName(ValoTheme.BUTTON_PRIMARY);
        with(username, password, loginMessage, login);
        login.addClickListener(this::login);
    }

    private void login(){
        try {
            Authentication token = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username.getValue(), password.getValue()));
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
