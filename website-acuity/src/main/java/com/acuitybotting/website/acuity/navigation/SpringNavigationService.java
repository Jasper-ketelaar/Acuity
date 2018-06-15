package com.acuitybotting.website.acuity.navigation;

import com.acuitybotting.website.acuity.views.ErrorView;
import com.acuitybotting.website.acuity.views.script_repository.views.ScriptListView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.internal.Conventions;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Governs view navigation of the app.
 */
@Component
@UIScope
public class SpringNavigationService extends SpringNavigator {

    private final ErrorView errorView;

    @Autowired
    public SpringNavigationService(ErrorView errorView) {
        this.errorView = errorView;
        setErrorView(errorView);
    }

    @Override
    public void navigateTo(String navigationState) {
        if (null == this.getViewProvider(navigationState)) {
            navigationState = Conventions.upperCamelToLowerHyphen(navigationState);
        }
        super.navigateTo(navigationState);
    }

    public void navigateToDefaultView() {
        if (!getState().isEmpty()) {
            return;
        }
        navigateTo(getViewId(ScriptListView.class));
    }

    public static void navigateTo(Class<? extends View> view, String... urlParams) {
        UI.getCurrent().getNavigator().navigateTo(getViewId(view) + Arrays.stream(urlParams).collect(Collectors.joining("/", "/", null)));
    }

    public static String getViewId(Class<? extends View> viewClass) {
        SpringView springView = viewClass.getAnnotation(SpringView.class);
        if (springView == null) {
            throw new IllegalArgumentException("The target class must be a @SpringView");
        }

        String name = springView.name();
        if (!"USE CONVENTIONS".equals(name)) return name;
        return Conventions.deriveMappingForView(viewClass, springView);
    }
}