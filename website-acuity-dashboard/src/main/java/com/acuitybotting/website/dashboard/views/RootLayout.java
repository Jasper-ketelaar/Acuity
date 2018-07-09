package com.acuitybotting.website.dashboard.views;

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

    private HorizontalLayout content = new HorizontalLayout();
    private VerticalLayout rootContent = new VerticalLayout();

    public RootLayout(){
        setSizeFull();
        setSpacing(false);
        setMargin(false);
        setPadding(false);

        getClassNames().add("acuity-root");

        add(createTopMenu());

        rootContent.getClassNames().add("acuity-root-content");
        rootContent.setSizeFull();
        rootContent.setMargin(false);
        rootContent.setPadding(false);
        rootContent.setSpacing(false);


        content.add(createLeft(), rootContent);
        content.expand(rootContent);
        content.setSizeFull();
        add(content);

    }

    private Component createLeft(){
        VerticalLayout leftMenu = new VerticalLayout();
        leftMenu.getClassNames().add("acuity-left-menu");
        leftMenu.setWidth("250px");
        leftMenu.setHeight("100%");
        leftMenu.setMargin(false);
        leftMenu.setSpacing(false);
        leftMenu.setPadding(false);

        Button profile = new Button("Profile", VaadinIcon.HOME.create());
        profile.setWidth("100%");
        profile.getClassNames().add("acuity-nav-button");
        leftMenu.add(profile);

        return leftMenu;
    }

    private Component createTopMenu(){
        HorizontalLayout topMenu = new HorizontalLayout();
        topMenu.getClassNames().add("acuity-top-menu");
        topMenu.setWidth("100%");
        topMenu.setHeight("50px");
        topMenu.setMargin(false);
        topMenu.setSpacing(false);
        topMenu.add(new Span("Acuity Botting"));
        return topMenu;
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        rootContent.removeAll();
        Component component = content.getElement().getComponent().get();
        rootContent.add(component);
        rootContent.expand(component);
    }
}
