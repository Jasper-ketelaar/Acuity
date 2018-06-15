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

    public void changeRepositoryAccess(String repositoryName, boolean privateRepository) throws IOException {
        Repository repository = getRepository(repositoryName).orElseThrow(() -> new RuntimeException("Failed to get repository."));
        RepositoryService service = new RepositoryService(getClient());
        service.editRepository(repository.setPrivate(privateRepository));
    }

    public void addCollaborator(String repositoryName, String... collaboratorGitHubUsernames) throws IOException {
        Repository repository = getRepository(repositoryName).orElseThrow(() -> new RuntimeException("Failed to get repository."));
        CollaboratorService collaboratorService = new CollaboratorService(getClient());
        for (String collaboratorGitHubUsername : collaboratorGitHubUsernames) {
            collaboratorService.addCollaborator(repository, collaboratorGitHubUsername);
        }
    }

    public Optional<Repository> getRepository(String repositoryName) throws IOException {
        RepositoryService service = new RepositoryService(getClient());
        return Optional.ofNullable(service.getRepository(getUsername(), repositoryName));
    }

    public String createRepo(String repositoryName) throws IOException {
        RepositoryService service = new RepositoryService(getClient());
        Repository repository = service.createRepository(new Repository().setName(repositoryName).setPrivate(true));
        return repository.getHtmlUrl();
    }

    public void downloadRepoAsZip(String repositoryName, File fileZip) throws IOException {
        String url = "/repos/" + getUsername() + "/" + repositoryName + "/zipball/master";
        try (InputStream stream = getClient().getStream(new GitHubRequest().setUri(url))){
           Files.copy(stream, fileZip.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private GitHubClient getClient(){
        return client;
    }
}
