package com.acuitybotting.website.dashboard.views;

import com.acuitybotting.website.dashboard.components.general.nav.NavigationButton;
import com.acuitybotting.website.dashboard.views.navigation.LeftMenu;
import com.acuitybotting.website.dashboard.views.navigation.TopMenu;
import com.acuitybotting.website.dashboard.views.user.Profile;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */

@Push
@BodySize(height = "100vh", width = "100vw")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@StyleSheet("/acuity.css")
public class RootLayout extends VerticalLayout implements RouterLayout {

    private TopMenu topMenu = new TopMenu();
    private LeftMenu leftMenu = new LeftMenu();
    private HorizontalLayout content = new HorizontalLayout();
    private VerticalLayout rootContent = new VerticalLayout();

    public RootLayout() {
        setSizeFull();
        setSpacing(false);
        setMargin(false);
        setPadding(false);

        getClassNames().add("acuity-root");

        rootContent.getClassNames().add("acuity-root-content");
        rootContent.setSizeFull();
        rootContent.setMargin(false);
        rootContent.setPadding(false);
        rootContent.setSpacing(false);

        content.add(leftMenu, rootContent);
        content.expand(rootContent);

        content.setSizeFull();
        add(topMenu, content);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        rootContent.removeAll();
        content.getElement().getComponent().ifPresent(component -> {
            rootContent.add(component);
            rootContent.expand(component);
        });
    }
}
