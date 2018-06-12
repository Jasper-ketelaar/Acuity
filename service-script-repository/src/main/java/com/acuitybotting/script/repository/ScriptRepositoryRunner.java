package com.acuitybotting.script.repository;

import com.acuitybotting.script.repository.service.ScriptRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by Zachary Herridge on 6/12/2018.
 */
@Component
public class ScriptRepositoryRunner implements CommandLineRunner{

    private final ScriptRepositoryService scriptRepositoryService;

    @Autowired
    public ScriptRepositoryRunner(ScriptRepositoryService scriptRepositoryService) {
        this.scriptRepositoryService = scriptRepositoryService;
    }

    private void downloadAndCompile() throws Exception {
        scriptRepositoryService.compileAndUpload("TestScript");
    }

    @Override
    public void run(String... strings) {
        try {
            downloadAndCompile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
