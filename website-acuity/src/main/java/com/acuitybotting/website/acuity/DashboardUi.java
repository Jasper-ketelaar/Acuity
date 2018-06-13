package com.acuitybotting.website.acuity;

import com.acuitybotting.website.acuity.navigation.DashboardNavigation;
import com.acuitybotting.website.acuity.navigation.NavigationManager;
import com.acuitybotting.website.acuity.views.TestView;
import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import kaesdingeling.hybridmenu.HybridMenu;
import kaesdingeling.hybridmenu.components.HMButton;
import kaesdingeling.hybridmenu.data.MenuConfig;
import kaesdingeling.hybridmenu.design.DesignItem;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@Theme("valo")
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
        HybridMenu hybridMenu = HybridMenu.get()
                .withInitNavigator(false)
                .withNaviContent(new VerticalLayout())
                .withConfig(MenuConfig.get().withDesignItem(DesignItem.getDarkDesign()))
                .build();

        hybridMenu.getLeftMenu().add(HMButton.get()
                .withCaption("Notification Builder")
                .withIcon(VaadinIcons.BELL)
                .withNavigateTo(TestView.class));

        setContent(hybridMenu);
        navigationManager.init(this, hybridMenu.getNaviContent());
    }
}
