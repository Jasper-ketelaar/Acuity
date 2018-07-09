package com.acuitybotting.website.dashboard.views.user;

import com.acuitybotting.website.dashboard.security.view.interfaces.UsersOnly;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */
@Route(value = "profile", layout = RootLayout.class)
public class Profile extends Div implements UsersOnly {

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        add(new Span("Logged in: " + UI.getCurrent().getSession().getAttribute("loggedIn")));
    }

    private void init(){

    }
}
