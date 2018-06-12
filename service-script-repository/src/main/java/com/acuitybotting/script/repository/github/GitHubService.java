package com.acuitybotting.script.repository.github;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by Zachary Herridge on 6/12/2018.
 */
@Service
public class GitHubService {

    private GitHubClient client = new GitHubClient();;

    public GitHubService() {
        client.setCredentials(System.getenv("github.username"), System.getenv("github.password"));
    }

    public void createRepo(String name, String user) throws IOException {
        RepositoryService service = new RepositoryService(getClient());
        CollaboratorService collaboratorService = new CollaboratorService(getClient());
        Repository repository = service.createRepository(new Repository().setName(name).setPrivate(true));
        collaboratorService.addCollaborator(repository, user);
    }

    private GitHubClient getClient(){
        return client;
    }
}
