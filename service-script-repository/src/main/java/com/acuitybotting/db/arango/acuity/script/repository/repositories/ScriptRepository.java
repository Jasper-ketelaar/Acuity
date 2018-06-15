package com.acuitybotting.db.arango.acuity.script.repository.repositories;

import com.acuitybotting.db.arango.acuity.script.repository.domain.Script;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;
import java.util.Set;

public interface ScriptRepository extends ArangoRepository<Script> {

    Set<Script> findAllByAuthorOrAccessLevel(String authorId, int accessLevel);

    Optional<Script> findByGithubRepoName(String githubRepoName);
}
