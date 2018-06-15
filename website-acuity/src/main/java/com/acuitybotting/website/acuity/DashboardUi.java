package com.acuitybotting.website.acuity;

import com.acuitybotting.db.arango.acuity.identities.service.AcuityIdentityService;
import com.acuitybotting.website.acuity.navigation.DashboardNavigationMenuService;
import com.acuitybotting.website.acuity.navigation.SpringNavigationService;
import com.vaadin.annotations.Push;
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
@Push
@SpringUI
public class DashboardUi extends UI {

    private final SpringNavigationService springNavigationService;
    private final DashboardNavigationMenuService dashboardNavigationMenuService;

    private final AcuityIdentityService acuityIdentityService;

    @Autowired
    public DashboardUi(SpringNavigationService springNavigationService, DashboardNavigationMenuService dashboardNavigationMenuService, AcuityIdentityService acuityIdentityService) {
        this.springNavigationService = springNavigationService;
        this.dashboardNavigationMenuService = dashboardNavigationMenuService;
        this.acuityIdentityService = acuityIdentityService;
        setNavigator(this.springNavigationService);
    }

    protected void init(VaadinRequest vaadinRequest) {
        HybridMenu hybridMenu = dashboardNavigationMenuService.build();
        setContent(hybridMenu);
        springNavigationService.init(this, hybridMenu.getNaviContent());
        springNavigationService.navigateToDefaultView();
        getUI().getSession().setAttribute(AcuityIdentityService.class, acuityIdentityService);
    }
}
