package com.acuitybotting.script.repository;

import com.acuitybotting.script.repository.compile.CompileService;
import com.acuitybotting.script.repository.github.GitHubService;
import com.acuitybotting.script.repository.obfuscator.ObfuscatorService;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

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

    private void downloadAndCompile() throws Exception {
        File downloadLocation = new File("TestScript.zip");
        gitHubService.downloadRepoAsZip("TestScript", downloadLocation);
        File unzipLocation = new File("TestScript");
        compileService.unzip(downloadLocation, unzipLocation);
        File outLocation = new File("TestScript-Out");
        compileService.compile(
                new File("C:\\Users\\zgher\\Documents\\RSPeer\\cache\\rspeer.jar"),
                unzipLocation,
                outLocation
        );
        File jarLocation = new File("TestScript.jar");
        compileService.jar(outLocation, jarLocation);
        File obfuscatedJarLocation = new File("TestScript-Obbed.jar");
        obfuscatorService.obfuscate(
                new File("C:\\Users\\zgher\\IdeaProjects\\Acuity\\service-script-repository\\src\\main\\resources\\allatori.jar"),
                new File("C:\\Users\\zgher\\IdeaProjects\\Acuity\\service-script-repository\\src\\main\\resources\\config-placeholder.xml"),
                new File("active.xml"),
                jarLocation,
                obfuscatedJarLocation
        );
        System.out.println("Done");
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
