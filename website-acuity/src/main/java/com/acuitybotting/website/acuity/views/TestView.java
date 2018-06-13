package com.acuitybotting.website.acuity.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;


/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@SpringView
public class TestView extends MVerticalLayout implements View{

    @PostConstruct
    void init(){
        addComponent(new Label("This is the default view"));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        init();
    }
}
