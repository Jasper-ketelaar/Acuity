package com.acuitybotting.website.acuity.navigation;

import com.acuitybotting.website.acuity.views.TestView;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@UIScope
@SpringComponent
public class DashboardSideNavigator extends MVerticalLayout {

    @PostConstruct
    public void init(){
        withComponent(createNavButton("Test", "TestView"));
        setWidth(300, Unit.PIXELS);
    }

    private Component createNavButton(String first, String view) {
        MButton button = new MButton(first);
        button.addClickListener(e-> getUI().getNavigator().navigateTo(view));
        button.addStyleName(ValoTheme.BUTTON_LARGE);
        button.addStyleName(ValoTheme.BUTTON_LINK);
        return button;
    }
}
