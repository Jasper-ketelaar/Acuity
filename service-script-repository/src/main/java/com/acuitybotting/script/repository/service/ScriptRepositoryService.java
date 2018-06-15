package com.acuitybotting.script.repository.service;

import com.acuitybotting.db.arango.acuity.script.repository.domain.Script;
import com.acuitybotting.db.arango.acuity.script.repository.domain.ScriptAuth;
import com.acuitybotting.db.arango.acuity.script.repository.repositories.ScriptAuthRepository;
import com.acuitybotting.db.arango.acuity.script.repository.repositories.ScriptRepository;
import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.acuitybotting.script.repository.compile.CompileService;
import com.acuitybotting.script.repository.github.GitHubService;
import com.acuitybotting.script.repository.obfuscator.ObfuscatorService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Service
public class ScriptRepositoryService {

    private final ScriptRepository scriptRepository;
    private final ScriptAuthRepository scriptAuthRepository;

    private final CompileService compileService;
    private final GitHubService gitHubService;
    private final ObfuscatorService obfuscatorService;

    @Autowired
    public ScriptRepositoryService(ScriptRepository scriptRepository, ScriptAuthRepository scriptAuthRepository, CompileService compileService, GitHubService gitHubService, ObfuscatorService obfuscatorService) {
        this.scriptRepository = scriptRepository;
        this.scriptAuthRepository = scriptAuthRepository;
        this.compileService = compileService;
        this.gitHubService = gitHubService;
        this.obfuscatorService = obfuscatorService;
    }

    public File downloadAndCompile(String repositoryName) throws Exception{
        Objects.requireNonNull(repositoryName);

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

    public boolean isAuthedForScript(String identityId, Script script){
        Objects.requireNonNull(script);

        if (script.getAccessLevel() == Script.ACCESS_PUBLIC) return true;
        if (identityId != null && script.getAuthor().getId().equals(identityId)) return true;
        return scriptAuthRepository.findAllByScriptAndPrincipal(script.getId(), identityId).stream().anyMatch(ScriptAuth::isActive);
    }

    public Collection<Script> findAllScripts(String identityID){
        Collection<Script> authed = Collections.emptyList();
        if (identityID != null) authed = scriptAuthRepository.findAllByPrincipal(identityID).stream().map(ScriptAuth::getScript).collect(Collectors.toList());
        Set<Script> allByAuthorOrAccessLevel = scriptRepository.findAllByAuthorOrAccessLevel(identityID, Script.ACCESS_PUBLIC);
        allByAuthorOrAccessLevel.addAll(authed);
        return allByAuthorOrAccessLevel;
    }

    public void addScriptAuth(Script script, AcuityIdentity principal, int authType, Long expiration){
        Objects.requireNonNull(script);
        Objects.requireNonNull(principal);

        if (expiration == null) expiration = 0L;
        ScriptAuth scriptAuth = new ScriptAuth();
        scriptAuth.setCreationTime(System.currentTimeMillis());
        scriptAuth.setExpirationTime(expiration);
        scriptAuth.setPrincipal(principal);
        scriptAuth.setScript(script);
        scriptAuth.setAuthType(authType);
        scriptAuthRepository.save(scriptAuth);
    }

    public Script createRepository(AcuityIdentity author, int access, String repositoryName, String title, String desc, String category) throws Exception {
        Objects.requireNonNull(author);
        Objects.requireNonNull(repositoryName);
        Objects.requireNonNull(title);
        Objects.requireNonNull(desc);
        Objects.requireNonNull(category);

        String url = gitHubService.createRepo(repositoryName);
        Script script = new Script();
        script.setGithubRepoName(repositoryName);
        script.setAuthor(author);
        script.setGithubUrl(url);
        script.setTitle(title);
        script.setAccessLevel(access);
        script.setCreationTime(System.currentTimeMillis());
        script.setDescription(desc);
        script.setCategory(category);
        return scriptRepository.save(script);
    }
}
