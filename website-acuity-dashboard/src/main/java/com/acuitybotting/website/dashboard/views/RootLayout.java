package com.acuitybotting.website.dashboard.views;

import com.acuitybotting.website.dashboard.views.navigation.LeftMenu;
import com.acuitybotting.website.dashboard.views.navigation.TopMenu;
import com.google.common.eventbus.EventBus;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */

@BodySize(height = "100vh", width = "100vw")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@StyleSheet("/acuity.css")
public class RootLayout extends VerticalLayout implements RouterLayout {

    private static EventBus globalEventBus = new EventBus();

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
        content.getElement().getComponent().ifPresent(component -> {
            rootContent.removeAll();
            rootContent.add(component);
            rootContent.expand(component);
        });
    }

    public static EventBus getGlobalEventBus() {
        return globalEventBus;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        System.out.println("Root attached");
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        System.out.println("Root detached");
    }
}
