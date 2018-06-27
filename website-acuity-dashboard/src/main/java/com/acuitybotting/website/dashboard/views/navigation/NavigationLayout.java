package com.acuitybotting.website.dashboard.views.navigation;

import com.acuitybotting.website.dashboard.views.RootLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouterLayout;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */
@ParentLayout(RootLayout.class)
public class NavigationLayout extends VerticalLayout implements RouterLayout {

    public NavigationLayout(){
        add(new Span("BAR"));
    }
}
