package com.acuitybotting.website.acuity.views.login;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@SpringView(name = "Login")
public class LoginView extends MVerticalLayout implements View {

    public LoginView() {
        MTextField username = new MTextField("Username");
        PasswordField password = new PasswordField("Password");
        MLabel loginMessage = new MLabel("").withVisible(false);
        MButton login = new MButton("Login").withStyleName(ValoTheme.BUTTON_PRIMARY);
        with(username, password, loginMessage, login);

        login.addClickListener(clickEvent -> {
            System.out.println("Login: " + username.getValue() + ", " + password.getValue());
        });
    }
}
