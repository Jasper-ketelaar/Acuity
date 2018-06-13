package com.acuitybotting.website.acuity.navigation;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.layouts.MPanel;

import javax.annotation.PostConstruct;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@SpringViewDisplay
public class DashboardViewDisplay extends MPanel implements ViewDisplay{

    @PostConstruct
    public void init(){
        setStyleName(ValoTheme.PANEL_BORDERLESS);
    }

    @Override
    public void showView(View view) {
        setContent((Component) view);
    }
}
