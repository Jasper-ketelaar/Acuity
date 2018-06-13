package com.acuitybotting.website.acuity;

import com.acuitybotting.website.acuity.navigation.DashboardNavigation;
import com.acuitybotting.website.acuity.navigation.NavigationManager;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import kaesdingeling.hybridmenu.HybridMenu;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@Theme("mytheme")
@Viewport("width=device-width,initial-scale=1.0,user-scalable=no")
@SpringUI
public class DashboardUi extends UI {

    private final NavigationManager navigationManager;
    private final DashboardNavigation dashboardNavigation;

    @Autowired
    public DashboardUi(NavigationManager navigationManager, DashboardNavigation dashboardNavigation) {
        this.navigationManager = navigationManager;
        this.dashboardNavigation = dashboardNavigation;
        setNavigator(this.navigationManager);
    }

    protected void init(VaadinRequest vaadinRequest) {
        HybridMenu hybridMenu = dashboardNavigation.build();
        setContent(hybridMenu);
        navigationManager.init(this, hybridMenu.getNaviContent());
    }
}
