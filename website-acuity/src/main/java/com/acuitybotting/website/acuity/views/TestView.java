package com.acuitybotting.website.acuity.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import kaesdingeling.hybridmenu.components.Notification;
import kaesdingeling.hybridmenu.components.NotificationCenter;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;


/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@ViewScope
@SpringView
public class TestView extends MVerticalLayout implements View{

    @PostConstruct
    void init(){
        addComponent(new MButton("Test").withListener(clickEvent -> {
            NotificationCenter notificationCenter = VaadinSession.getCurrent().getAttribute(NotificationCenter.class);
            notificationCenter.add(Notification.get().withTitle("Working"));
        }));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
