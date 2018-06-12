package com.acuitybotting.script.repository;

import com.acuitybotting.script.repository.compile.CompileService;
import com.acuitybotting.script.repository.github.GitHubService;
import com.acuitybotting.script.repository.obfuscator.ObfuscatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by Zachary Herridge on 6/12/2018.
 */
@Component
public class ScriptRepositoryRunner implements CommandLineRunner{

    private final CompileService compileService;
    private final GitHubService gitHubService;
    private final ObfuscatorService obfuscatorService;

    @Autowired
    public ScriptRepositoryRunner(CompileService compileService, GitHubService gitHubService, ObfuscatorService obfuscatorService) {
        this.compileService = compileService;
        this.gitHubService = gitHubService;
        this.obfuscatorService = obfuscatorService;
    }

    @Override
    public void run(String... strings) throws Exception {
        File file = new File("Acuity.zip");
        gitHubService.downloadRepoAsZip("Acuity", file);
        compileService.unzip(file, new File("Acuity"));
    }
}
