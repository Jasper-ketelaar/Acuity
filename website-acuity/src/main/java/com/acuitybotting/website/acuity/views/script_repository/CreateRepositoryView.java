package com.acuitybotting.website.acuity.views.script_repository;

import com.acuitybotting.db.arango.acuity.script.repository.domain.Script;
import com.acuitybotting.script.repository.service.ScriptRepositoryService;
import com.acuitybotting.security.acuity.spring.AcuitySecurityContext;
import com.acuitybotting.website.acuity.security.ViewAccess;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@ViewAccess("hasAuthority('BASIC_USER')")
@SpringView(name = "RequestScriptRepo")
public class CreateRepositoryView extends MVerticalLayout implements View{

    private final ScriptRepositoryService scriptRepositoryService;

    private MTextField repositoryName = new MTextField("Repository Name");
    private MTextField githubUsername = new MTextField("Github Username");
    private MLabel errorLabel = new MLabel().withVisible(false);

    @Autowired
    public CreateRepositoryView(ScriptRepositoryService scriptRepositoryService) {
        this.scriptRepositoryService = scriptRepositoryService;
    }

    @PostConstruct
    public void init(){
        with(repositoryName, githubUsername, errorLabel, new MButton("Request").addClickListener(this::createRepo));
    }

    private void createRepo(){
        try {
            Script script = scriptRepositoryService.createRepository(AcuitySecurityContext.getPrincipalKey(), repositoryName.getValue(), githubUsername.getValue());
            if (script != null){
                getUI().getNavigator().navigateTo("Script/" + script.getKey());
            }
        } catch (Exception e) {
            errorLabel.withValue(e.getMessage()).withVisible(true);
            e.printStackTrace();
        }
    }
}