package com.acuitybotting.website.acuity.views.script_repository;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.UI;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@ViewScope
@SpringView(name = "ScripRepository")
public class ScriptListView extends MVerticalLayout implements View{

    @PostConstruct
    public void init(){
        addComponent(new MLabel("User: " + SecurityContextHolder.getContext().getAuthentication()));
        addComponent(new MLabel("Session: " + UI.getCurrent().getSession().getAttribute("username")));
        addComponent(new MLabel("Session: " + UI.getCurrent().getSession().getAttribute("test")));
    }
}
