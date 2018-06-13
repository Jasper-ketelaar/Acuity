package com.acuitybotting.website.acuity.views;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;


/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@UIScope
@SpringView(name = "TestView")
public class TestView extends MVerticalLayout implements View{

    @PostConstruct
    void init(){
        addComponent(new Label("This is the default view"));
    }
}
