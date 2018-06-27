package com.acuitybotting.website.dashboard.security.view.interfaces;

import com.acuitybotting.website.dashboard.views.login.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */
public interface UsersOnly extends BeforeEnterObserver {

    @Override
    default void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        Object loggedIn = UI.getCurrent().getSession().getAttribute("loggedIn");
        if (loggedIn == null){
            beforeEnterEvent.rerouteTo(LoginView.class);
        }
    }
}
