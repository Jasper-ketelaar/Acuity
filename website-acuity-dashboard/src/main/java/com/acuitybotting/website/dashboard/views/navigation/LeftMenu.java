package com.acuitybotting.website.dashboard.views.navigation;

import com.acuitybotting.website.dashboard.components.general.nav.NavigationButton;
import com.acuitybotting.website.dashboard.views.user.Profile;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */
public class LeftMenu extends VerticalLayout {

    public LeftMenu() {
        getClassNames().add("acuity-left-menu");
        setWidth("250px");
        setHeight("100%");
        setMargin(false);
        setSpacing(false);
        setPadding(false);

        add(new NavigationButton("Profile", VaadinIcon.HOME.create(), Profile.class));
    }
}
