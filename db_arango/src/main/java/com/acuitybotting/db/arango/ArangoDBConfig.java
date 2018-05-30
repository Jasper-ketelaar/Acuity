package com.acuitybotting.db.arango;

import com.acuitybotting.aws.common.SecretManager;
import com.acuitybotting.db.arango.security.Credentials;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDB.Builder;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.AbstractArangoConfiguration;
import com.google.gson.Gson;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableArangoRepositories(basePackages = { "com.acuitybotting.db.arango.repositories" })
public class ArangoDBConfig extends AbstractArangoConfiguration{


    @Override
    public Builder arango() {
        Credentials credentials = new Gson().fromJson(SecretManager.getSecret("secretsmanager.us-east-1.amazonaws.com", "us-east-1", "ArangoDBAccess").orElseThrow(() -> new RuntimeException("Failed to access Arango secret.")), Credentials.class);
        return new ArangoDB.Builder().host(credentials.getHost(), Integer.parseInt(credentials.getPort())).user(credentials.getUsername()).password(credentials.getPassword());
    }

    @Override
    public String database() {
        return "AcuityBotting";
    }

}
