package com.acuitybotting.website.acuity.navigation;

import com.acuitybotting.website.acuity.views.TestView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;
import kaesdingeling.hybridmenu.HybridMenu;
import kaesdingeling.hybridmenu.components.HMButton;
import kaesdingeling.hybridmenu.data.MenuConfig;
import kaesdingeling.hybridmenu.design.DesignItem;
import org.springframework.stereotype.Component;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@Component
@UIScope
public class DashboardNavigation {

    public HybridMenu getContent(){
        HybridMenu hybridMenu = HybridMenu.get()
                .withInitNavigator(false)
                .withNaviContent(new VerticalLayout())
                .withConfig(MenuConfig.get().withDesignItem(DesignItem.getDarkDesign()))
                .build();

        hybridMenu.getLeftMenu().add(HMButton.get()
                .withCaption("Notification Builder")
                .withIcon(VaadinIcons.BELL)
                .withNavigateTo(TestView.class));

        return hybridMenu;
    }
}
