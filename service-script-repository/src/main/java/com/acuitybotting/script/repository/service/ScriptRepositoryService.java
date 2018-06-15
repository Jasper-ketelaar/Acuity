package com.acuitybotting.script.repository.service;

import com.acuitybotting.db.arango.acuity.script.repository.domain.Script;
import com.acuitybotting.db.arango.acuity.script.repository.repositories.ScriptRepository;
import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityIdentityService;
import com.acuitybotting.script.repository.compile.CompileService;
import com.acuitybotting.script.repository.github.GitHubService;
import com.acuitybotting.script.repository.obfuscator.ObfuscatorService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Getter
@Service
public class ScriptRepositoryService {

    private final ScriptRepository scriptRepository;
    private final CompileService compileService;
    private final GitHubService gitHubService;
    private final ObfuscatorService obfuscatorService;

    @Autowired
    public ScriptRepositoryService(ScriptRepository scriptRepository, CompileService compileService, GitHubService gitHubService, ObfuscatorService obfuscatorService) {
        this.scriptRepository = scriptRepository;
        this.compileService = compileService;
        this.gitHubService = gitHubService;
        this.obfuscatorService = obfuscatorService;
    }

    public File downloadAndCompile(String repositoryName) throws Exception{
        File parentFile = new File(UUID.randomUUID().toString().replaceAll("\\.", "-"));
        if (!parentFile.mkdirs()) throw new RuntimeException("Failed to create temp file.");

        File downloadLocation = new File(parentFile, repositoryName + ".zip");
        gitHubService.downloadRepoAsZip(repositoryName, downloadLocation);

        File unzipLocation = new File(parentFile, repositoryName);
        compileService.unzip(downloadLocation, unzipLocation);

        File outLocation = new File(parentFile, "out");
        compileService.compile(
                new File("C:\\Users\\zgher\\Documents\\RSPeer\\cache\\rspeer.jar"),
                unzipLocation,
                outLocation
        );

        File jarLocation = new File(parentFile,repositoryName + ".jar");
        compileService.jar(outLocation, jarLocation);

        File obfuscatedJarLocation = new File(parentFile,repositoryName + "-Obbed.jar");
        obfuscatorService.obfuscate(
                new File("C:\\Users\\zgher\\IdeaProjects\\Acuity\\service-script-repository\\src\\main\\resources\\allatori.jar"),
                new File("C:\\Users\\zgher\\IdeaProjects\\Acuity\\service-script-repository\\src\\main\\resources\\config-placeholder.xml"),
                new File(parentFile,"active.xml"),
                jarLocation,
                obfuscatedJarLocation
        );

        return parentFile;
    }

    public String compileAndUpload(String repositoryName) throws Exception{
        File parentFile = new File(UUID.randomUUID().toString().replaceAll("\\.", "-"));
        if (!parentFile.mkdirs()) throw new RuntimeException("Failed to create temp file.");

        File downloadLocation = new File(parentFile, repositoryName + ".zip");
        gitHubService.downloadRepoAsZip(repositoryName, downloadLocation);

        File unzipLocation = new File(parentFile, repositoryName);
        compileService.unzip(downloadLocation, unzipLocation);

        File outLocation = new File(parentFile, "out");
        compileService.compile(
                new File("C:\\Users\\zgher\\Documents\\RSPeer\\cache\\rspeer.jar"),
                unzipLocation,
                outLocation
        );

        File jarLocation = new File(parentFile,repositoryName + ".jar");
        compileService.jar(outLocation, jarLocation);

        File obfuscatedJarLocation = new File(parentFile,repositoryName + "-Obbed.jar");
        obfuscatorService.obfuscate(
                new File("C:\\Users\\zgher\\IdeaProjects\\Acuity\\service-script-repository\\src\\main\\resources\\allatori.jar"),
                new File("C:\\Users\\zgher\\IdeaProjects\\Acuity\\service-script-repository\\src\\main\\resources\\config-placeholder.xml"),
                new File(parentFile,"active.xml"),
                jarLocation,
                obfuscatedJarLocation
        );

        String jarUrl = uploadJar(obfuscatedJarLocation);

        parentFile.delete();

        return jarUrl;
    }

    private String uploadJar(File file){
        return "";
    }

    public Script createRepository(AcuityIdentity author, String repositoryName, String title, String desc, String category) throws Exception {
        String url = gitHubService.createRepo(repositoryName);
        Script script = new Script();
        script.setGithubRepoName(repositoryName);
        script.setAuthor(author);
        script.setGithubUrl(url);
        script.setTitle(title);
        script.setCreationTime(System.currentTimeMillis());
        script.setDescription(desc);
        script.setCategory(category);
        return scriptRepository.save(script);
    }
}
