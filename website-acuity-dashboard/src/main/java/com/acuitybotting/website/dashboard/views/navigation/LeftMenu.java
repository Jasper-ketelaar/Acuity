package com.acuitybotting.website.dashboard.views.navigation;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */
public class LeftMenu extends VerticalLayout {

    public LeftMenu() {
        setWidth("250px");
        setHeight("100%");
        getStyle().set("min-width", "250px");
        getStyle().set("max-width", "250px");
        setMargin(false);
        setPadding(false);
        setSpacing(false);
    }
}
