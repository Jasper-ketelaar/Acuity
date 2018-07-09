package com.acuitybotting.db.arango.path_finding.repositories.xtea;

import com.acuitybotting.db.arango.path_finding.domain.xtea.SceneEntityDefinition;
import com.acuitybotting.db.arango.utils.ArangoUtils;
import com.arangodb.springframework.repository.ArangoRepository;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public interface SceneEntityDefinitionRepository extends ArangoRepository<SceneEntityDefinition>{

    default int loadFrom(File file) throws IOException {
        deleteAll();
        Gson gson = new Gson();
        File[] files = file.listFiles();
        Set<SceneEntityDefinition> sceneEntityDefinitions = new HashSet<>();
        for (File child : files) {
            SceneEntityDefinition def = gson.fromJson(Files.readAllLines(child.toPath()).stream().collect(Collectors.joining("\n")), SceneEntityDefinition.class);
            for (int i = 0; i < def.getActions().length; i++) {
                if (def.getActions()[i] == null) {
                    def.getActions()[i] = "null";
                }
            }
            sceneEntityDefinitions.add(def);
        }

        ArangoUtils.saveAll(this, 400, sceneEntityDefinitions);
        return sceneEntityDefinitions.size();
    }
}
