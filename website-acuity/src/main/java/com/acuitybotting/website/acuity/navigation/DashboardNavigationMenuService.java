package com.acuitybotting.website.acuity.navigation;

import com.acuitybotting.website.acuity.views.TestView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;
import kaesdingeling.hybridmenu.HybridMenu;
import kaesdingeling.hybridmenu.components.*;
import kaesdingeling.hybridmenu.data.MenuConfig;
import kaesdingeling.hybridmenu.data.enums.ToggleMode;
import kaesdingeling.hybridmenu.design.DesignItem;
import org.springframework.stereotype.Component;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@Component
@UIScope
public class DashboardNavigationMenuService {

    public HybridMenu build(){
        HybridMenu hybridMenu = HybridMenu.get()
                .withInitNavigator(false)
                .withNaviContent(new VerticalLayout())
                .withConfig(MenuConfig.get().withDesignItem(DesignItem.getDarkDesign()))
                .build();

        buildTop(hybridMenu);
        buildLeft(hybridMenu);

        return hybridMenu;
    }

    private void buildTop(HybridMenu hybridMenu){
        TopMenu topMenu = hybridMenu.getTopMenu();

        HMButton toggleButton = HMButton.get();
        topMenu.add(toggleButton
                .withIcon(VaadinIcons.MINUS_CIRCLE)
                .withClickListener(clickEvent -> toggleLeftMenu(toggleButton, hybridMenu))
                .withDescription("Toggle Menu")
                .withNavigateTo(TestView.class));

        hybridMenu.getNotificationCenter()
                .setNotiButton(topMenu.add(HMButton.get()
                        .withDescription("Notifications")));
    }

    private void toggleLeftMenu(HMButton toggleButton, HybridMenu hybridMenu) {
        hybridMenu.getLeftMenu().toggleSize();
        ToggleMode toggleMode = hybridMenu.getLeftMenu().getToggleMode();
        toggleButton.setIcon(toggleMode.equals(ToggleMode.MINIMAL) ? VaadinIcons.PLUS_CIRCLE : VaadinIcons.MINUS_CIRCLE);
    }

    public void toggleLeftMenu(boolean large){

    }

    private void buildLeft(HybridMenu hybridMenu){
        LeftMenu leftMenu = hybridMenu.getLeftMenu();

        leftMenu.add(HMLabel.get()
                .withCaption("<b>Acuity</b> Botting")
                .withIcon(new ThemeResource("images/logos/acuity_white.png")));

        leftMenu.add(HMButton.get()
                .withCaption("Notification Builder")
                .withIcon(VaadinIcons.BELL)
                .withNavigateTo(TestView.class));
    }
}
