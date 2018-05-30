package com.acuitybotting.db.arango;

import com.acuitybotting.aws.common.SecretManager;
import com.acuitybotting.db.arango.repositories.TileFlagRepository;
import com.acuitybotting.db.arango.security.Credentials;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDB.Builder;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.AbstractArangoConfiguration;
import com.arangodb.springframework.core.ArangoOperations;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableArangoRepositories(basePackages = { "com.acuitybotting.db.arango" })
public class ArangoDBConfig extends AbstractArangoConfiguration{

    @Override
    public Builder arango() {
        Credentials credentials = new Gson().fromJson(SecretManager.getSecret("secretsmanager.us-east-1.amazonaws.com", "us-east-1", "DbCredentials").orElseThrow(() -> new RuntimeException("Failed to access Arango secret.")), Credentials.class);
        return new ArangoDB.Builder().host(credentials.getArangoHost(), Integer.parseInt(credentials.getArangoPort())).user(credentials.getArangoUsername()).password(credentials.getArangoPassword());
    }

    @Override
    public String database() {
        return "AcuityBotting";
    }
}
