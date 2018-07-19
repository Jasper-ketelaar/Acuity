package com.acuitybotting.website.dashboard.views.user;

import com.acuitybotting.data.flow.messaging.services.Message;
import com.acuitybotting.website.dashboard.security.view.interfaces.UsersOnly;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.google.common.eventbus.Subscribe;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */
@Route(value = "profile", layout = RootLayout.class)
public class Profile extends Div implements UsersOnly {

    private Span lastMessage = new Span();

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        add(new Span("Logged in: " + UI.getCurrent().getSession().getAttribute("loggedIn")), lastMessage);
    }

    @Subscribe
    public void handleOrderCreatedEvent(Message o) {
        System.out.println("GG");
        getUI().ifPresent(ui -> ui.access(() -> lastMessage.setText(o.toString())));
    }

    private void init(){

    }
}
