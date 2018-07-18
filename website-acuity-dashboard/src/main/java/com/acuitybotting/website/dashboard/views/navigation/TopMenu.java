package com.acuitybotting.website.dashboard.views.navigation;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * Created by Zachary Herridge on 7/18/2018.
 */
public class TopMenu extends HorizontalLayout {

    public TopMenu() {
        getClassNames().add("acuity-top-menu");
        setWidth("100%");
        setHeight("50px");
        setMargin(false);
        setSpacing(false);
        add(new Span("Acuity Botting"));
    }
}
