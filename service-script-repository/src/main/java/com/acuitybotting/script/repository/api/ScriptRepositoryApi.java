package com.acuitybotting.script.repository.api;

import com.acuitybotting.script.repository.service.ScriptRepositoryService;
import com.acuitybotting.security.acuity.web.AcuityWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/script/repository")
public class ScriptRepositoryApi {

    private final ScriptRepositoryService scriptRepositoryService;

    @Autowired
    public ScriptRepositoryApi(ScriptRepositoryService scriptRepositoryService) {
        this.scriptRepositoryService = scriptRepositoryService;
    }

    @PreAuthorize("hasAnyAuthority(Owners)")
    @RequestMapping(method = RequestMethod.POST, value = "compile-repo")
    public void compile(@RequestBody String repoName) throws Exception {
        File file = scriptRepositoryService.downloadAndCompile(repoName);
    }

    @PreAuthorize("hasAnyAuthority(BASIC_USER)")
    @RequestMapping(method = RequestMethod.POST, value = "request-repo")
    public String requestRepo(@RequestBody Map<String, String> body) throws IOException {
        String repoName = body.get("repoName");
        String githubUsername = body.get("githubUsername");
        if (repoName == null || githubUsername == null) throw new RuntimeException("Body must contain 'repoName' and 'githubUsername'.");
        return scriptRepositoryService.getGitHubService().createRepo(AcuityWebSecurity.getPrincipal().getUsername() + "-" + repoName, githubUsername);
    }
}
