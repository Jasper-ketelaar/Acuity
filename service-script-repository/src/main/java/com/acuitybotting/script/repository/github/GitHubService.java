package com.acuitybotting.script.repository.github;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/12/2018.
 */
@Service
public class GitHubService {

    private GitHubClient client = new GitHubClient();

    public GitHubService() {
        client.setCredentials(getEmail(), System.getenv("github.password"));
    }

    private String getEmail(){
        return System.getenv("github.email");
    }

    private String getUsername(){
        return System.getenv("github.username");
    }

    public Optional<Repository> getRepository(String name) throws IOException {
        RepositoryService service = new RepositoryService(getClient());
        return Optional.ofNullable(service.getRepository("ZachHerridge", name));
    }

    public String createRepo(String name, String user) throws IOException {
        RepositoryService service = new RepositoryService(getClient());
        CollaboratorService collaboratorService = new CollaboratorService(getClient());
        Repository repository = service.createRepository(new Repository().setName(name).setPrivate(true));
        collaboratorService.addCollaborator(repository, user);
        return repository.getHtmlUrl();
    }

    public void downloadRepoAsZip(String name, File location) throws IOException {
        String url = "/repos/" + getUsername() + "/" + name + "/zipball/master";
        try (InputStream stream = getClient().getStream(new GitHubRequest().setUri(url))){
           Files.copy(stream, location.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private GitHubClient getClient(){
        return client;
    }
}
