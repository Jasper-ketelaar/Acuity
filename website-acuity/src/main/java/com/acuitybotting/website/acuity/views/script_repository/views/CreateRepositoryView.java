package com.acuitybotting.website.acuity.views.script_repository.views;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.acuitybotting.db.arango.acuity.script.repository.domain.Script;
import com.acuitybotting.script.repository.service.ScriptRepositoryService;
import com.acuitybotting.website.acuity.navigation.SpringNavigationService;
import com.acuitybotting.website.acuity.notification.Notifications;
import com.acuitybotting.website.acuity.security.AcuityIdentityContext;
import com.acuitybotting.website.acuity.security.ViewAccess;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;

import static com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity.PROPERTY_GITHUB_USERNAME;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@ViewAccess("hasAuthority('BASIC_USER')")
@SpringView(name = "RequestScriptRepo")
public class CreateRepositoryView extends MVerticalLayout implements View{

    private final ScriptRepositoryService scriptRepositoryService;

    private MTextField repositoryName = new MTextField("Repository Name");
    private MTextField githubUsername = new MTextField("Github Username", AcuityIdentityContext.getProperty(PROPERTY_GITHUB_USERNAME, String.class).orElse(""));
    private MTextField scriptTitle = new MTextField("Script Title");
    private TextArea scriptDesc = new TextArea("Desc");
    private ComboBox<String> scriptCategory = new ComboBox<>("Script Category", Script.getCategories());

    @Autowired
    public CreateRepositoryView(ScriptRepositoryService scriptRepositoryService) {
        this.scriptRepositoryService = scriptRepositoryService;
    }

    @PostConstruct
    public void init(){
        with(
                repositoryName,
                githubUsername,
                scriptTitle,
                scriptCategory,
                scriptDesc,
                new MButton("Request").addClickListener(this::createRepo));
    }

    private void createRepo(){
        try {
            Script script = scriptRepositoryService.createRepository(
                    AcuityIdentityContext.getCurrent().orElseThrow(() -> new IllegalStateException("Invalid identity.")),
                    Script.ACCESS_PUBLIC,
                    repositoryName.getValue(),
                    scriptTitle.getValue(),
                    scriptDesc.getValue(),
                    scriptCategory.getSelectedItem().orElse(null)
            );
            if (script != null){
                SpringNavigationService.navigateTo(ScriptView.class, script.getKey());
                scriptRepositoryService.getGitHubService().addCollaborator(script.getGithubRepoName(), githubUsername.getValue());
                AcuityIdentityContext.putProperty(PROPERTY_GITHUB_USERNAME, githubUsername.getValue());
            }
        } catch (Exception e) {
            Notifications.displayWarning(e);
            e.printStackTrace();
        }
    }
}
