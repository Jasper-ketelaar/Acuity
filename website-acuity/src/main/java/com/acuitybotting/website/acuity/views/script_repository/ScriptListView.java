package com.acuitybotting.website.acuity.views.script_repository;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@SpringView(name = "ScripRepository")
public class ScriptListView extends MVerticalLayout implements View{

    @PostConstruct
    public void init(){

    }
}
