package com.acuitybotting.website.acuity.views.script_repository.components;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityIdentityService;
import com.acuitybotting.db.arango.acuity.script.repository.domain.Script;
import com.acuitybotting.db.arango.acuity.script.repository.domain.ScriptAuth;
import com.acuitybotting.script.repository.service.ScriptRepositoryService;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.button.PrimaryButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;

/**
 * Created by Zachary Herridge on 6/15/2018.
 */
@SpringComponent
public class ScriptAuthManagementComponent extends MVerticalLayout {

    private final AcuityIdentityService acuityIdentityService;
    private final ScriptRepositoryService scriptRepositoryService;

    private Script script;
    private MGrid<ScriptAuth> scriptAuthGrid = new MGrid<>(ScriptAuth.class);
    private MTextField authUsername = new MTextField("User Key");

    @Autowired
    public ScriptAuthManagementComponent(AcuityIdentityService acuityIdentityService, ScriptRepositoryService scriptRepositoryService) {
        this.acuityIdentityService = acuityIdentityService;
        this.scriptRepositoryService = scriptRepositoryService;
    }

    @PostConstruct
    private void withScript(){
        with(new MHorizontalLayout().with(
                authUsername,
                new PrimaryButton("Add Auth", clickEvent -> addAuth())
        ));

        with(scriptAuthGrid);
    }

    public ScriptAuthManagementComponent withScript(Script script) {
        this.script = script;

        scriptAuthGrid.setDataProvider(DataProvider.ofCollection(
                scriptRepositoryService.getScriptAuthRepository().findAllByScript(script.getId())
        ));
        scriptAuthGrid.getDataProvider().refreshAll();
        return this;
    }

    private void addAuth(){
        AcuityIdentity acuityIdentity = acuityIdentityService.getIdentityRepository().findById(authUsername.getValue()).orElse(null);
        scriptRepositoryService.addScriptAuth(script, acuityIdentity, Script.ACCESS_PRIVATE, null);
        getUI().access(() -> scriptAuthGrid.getDataProvider().refreshAll());
    }
}
