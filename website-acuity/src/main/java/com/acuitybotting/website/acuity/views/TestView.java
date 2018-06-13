package com.acuitybotting.website.acuity.views;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;


/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@UIScope
@SpringView(name = "TestView")
public class TestView extends VerticalLayout implements View{

    @PostConstruct
    void init(){
        addComponent(new Label("This is the default view"));
    }
}
