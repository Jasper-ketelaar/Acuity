package com.acuitybotting.website.dashboard.components.general.nav;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * Created by Zachary Herridge on 7/18/2018.
 */
public class NavigationButton extends HorizontalLayout {

    public NavigationButton(String profile, Icon icon, Class<? extends Component> view) {
        add(icon);
        add(new Span(profile));
        setWidth("100%");
        getClassNames().add("acuity-nav-button");
        getElement().addEventListener("click", domEvent -> getUI().ifPresent(ui -> ui.navigate(view)));
    }
}
