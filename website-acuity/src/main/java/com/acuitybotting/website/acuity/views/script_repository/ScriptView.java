package com.acuitybotting.website.acuity.views.script_repository;

import com.acuitybotting.db.arango.acuity.script.repository.domain.Script;
import com.acuitybotting.script.repository.service.ScriptRepositoryService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Created by Zachary Herridge on 6/14/2018.
 */
@ViewScope
@SpringView(name = "Script")
public class ScriptView extends MVerticalLayout implements View {

    private final ScriptRepositoryService scriptRepositoryService;
    private Script script;

    @Autowired
    public ScriptView(ScriptRepositoryService scriptRepositoryService) {
        this.scriptRepositoryService = scriptRepositoryService;
    }

    public void init(){
        with(new MLabel("Script Key", script.getKey()));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        scriptRepositoryService.getScriptRepository().findById(event.getParameters()).orElseThrow(() -> new RuntimeException("Failed to load script"));
        init();
    }
}
