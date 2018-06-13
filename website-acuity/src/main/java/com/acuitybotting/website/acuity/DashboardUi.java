package com.acuitybotting.website.acuity;

import com.acuitybotting.website.acuity.navigation.DashboardSideNavigator;
import com.acuitybotting.website.acuity.navigation.DashboardViewDisplay;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.layouts.MHorizontalLayout;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@Theme("valo")
@SpringUI
public class DashboardUi extends UI {

    private final DashboardSideNavigator sideNavigator;
    private final DashboardViewDisplay viewDisplay;

    @Autowired
    public DashboardUi(DashboardSideNavigator sideNavigator, DashboardViewDisplay viewDisplay) {
        this.sideNavigator = sideNavigator;
        this.viewDisplay = viewDisplay;
    }

    protected void init(VaadinRequest vaadinRequest) {
        setContent(
                new MHorizontalLayout()
                        .add(sideNavigator)
                        .expand(viewDisplay)
                        .withFullHeight()
        );
    }
}
