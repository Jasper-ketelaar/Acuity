package com.acuitybotting.website.acuity.views.script_repository.views;

import com.acuitybotting.db.arango.acuity.script.repository.domain.Script;
import com.acuitybotting.script.repository.service.ScriptRepositoryService;
import com.acuitybotting.security.acuity.spring.AcuityPrincipalContext;
import com.acuitybotting.website.acuity.notification.Notifications;
import com.acuitybotting.website.acuity.security.AcuityIdentityContext;
import com.acuitybotting.website.acuity.views.script_repository.components.ScriptAuthManagementComponent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Created by Zachary Herridge on 6/14/2018.
 */
@ViewScope
@SpringView(name = "Script")
public class ScriptView extends MVerticalLayout implements View {

    private final ScriptRepositoryService scriptRepositoryService;
    private Script script;

    private final ScriptAuthManagementComponent managementComponent;

    private MLabel scriptKey = new MLabel("Script Key", "");
    private MLabel scriptGitHubUrl = new MLabel("Script Repo Url", "");
    private MButton compile = new MButton("Compile", clickEvent -> compileScript()).withVisible(false);

    @Autowired
    public ScriptView(ScriptRepositoryService scriptRepositoryService, ScriptAuthManagementComponent managementComponent) {
        this.scriptRepositoryService = scriptRepositoryService;
        this.managementComponent = managementComponent;
    }

    @PostConstruct
    public void init(){
        with(compile, scriptKey, scriptGitHubUrl, managementComponent.withVisible(false));
    }

    public void withScript(Script script){
        this.script = script;

        scriptKey.setValue(script.getKey());
        scriptGitHubUrl.setValue(script.getGithubUrl());

        if (AcuityIdentityContext.isCurrent(script.getAuthor())) managementComponent.withScript(script).setVisible(true);
        if (AcuityPrincipalContext.hasRole("OWNER")) compile.setVisible(true);
    }

    private void compileScript(){
        try {
            File file = scriptRepositoryService.downloadAndCompile(script.getGithubRepoName());
            Notifications.displayInfo("Compile complete.");
        }
        catch (Exception e){
            e.printStackTrace();
            Notifications.displayWarning(e);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Script script = scriptRepositoryService.getScriptRepository().findById(event.getParameters()).orElseThrow(() -> new RuntimeException("Failed to load script"));
        if (script != null && scriptRepositoryService.isAuthedForScript(AcuityIdentityContext.getIdNullSafe(), script)) withScript(script);
    }
}
