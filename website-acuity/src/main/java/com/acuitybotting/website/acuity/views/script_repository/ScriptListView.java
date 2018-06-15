package com.acuitybotting.website.acuity.views.script_repository;

import com.acuitybotting.db.arango.acuity.script.repository.domain.Script;
import com.acuitybotting.script.repository.service.ScriptRepositoryService;
import com.acuitybotting.website.acuity.navigation.SpringNavigationService;
import com.acuitybotting.website.acuity.security.AcuityIdentityContext;
import com.google.common.collect.Lists;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.QPageRequest;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@ViewScope
@SpringView(name = "ScripRepository")
public class ScriptListView extends MVerticalLayout implements View{

    private final ScriptRepositoryService scriptRepositoryService;

    private MGrid<Script> scriptMGrid = new MGrid<>(Script.class);

    @Autowired
    public ScriptListView(ScriptRepositoryService scriptRepositoryService) {
        this.scriptRepositoryService = scriptRepositoryService;
    }

    @PostConstruct
    public void init(){
        withComponent(scriptMGrid.withHeight(90, Unit.PERCENTAGE));
        scriptMGrid.setDataProvider(DataProvider.ofCollection(Lists.newArrayList(scriptRepositoryService.getScriptRepository().findAll())));

        if (AcuityIdentityContext.isLoggedIn()) with(new MButton("Create Repo", clickEvent -> SpringNavigationService.navigateTo(CreateRepositoryView.class)));
    }
}
