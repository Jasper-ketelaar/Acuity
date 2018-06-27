package com.acuitybotting.website.dashboard.views.login;

import com.acuitybotting.website.dashboard.views.navigation.NavigationLayout;
import com.acuitybotting.website.dashboard.views.user.Profile;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */
@Route(value = "login", layout = NavigationLayout.class)
public class LoginView extends VerticalLayout{

    public LoginView() {
        add(new TextField("Username"));
        add(new PasswordField("Password"));

        Button login = new Button("Login");
        login.addClickListener(buttonClickEvent -> {
            getUI().ifPresent(ui -> {
                ui.getSession().setAttribute("loggedIn", true);
                ui.navigate(Profile.class);
            });
        });
        add(login);
    }
}
