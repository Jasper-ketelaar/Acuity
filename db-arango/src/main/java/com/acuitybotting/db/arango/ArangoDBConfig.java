package com.acuitybotting.db.arango;

import com.acuitybotting.db.arango.security.Credentials;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDB.Builder;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.AbstractArangoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableArangoRepositories(basePackages = { "com.acuitybotting.db.arango" })
public class ArangoDBConfig extends AbstractArangoConfiguration{

    @Override
    public Builder arango() {
        Credentials credentials = null; //= new Gson().fromJson(SecretManager.getSecret("secretsmanager.us-east-1.amazonaws.com", "us-east-1", "DbCredentials").orElseThrow(() -> new RuntimeException("Failed to access Arango secret.")), Credentials.class);
        return new ArangoDB.Builder().host(credentials.getArangoHost(), Integer.parseInt(credentials.getArangoPort())).user(credentials.getArangoUsername()).password(credentials.getArangoPassword());
    }

    @Override
    public String database() {
        return "AcuityBotting";
    }
}
