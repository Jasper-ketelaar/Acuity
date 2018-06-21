package com.acuitybotting.db.arango;

import com.acuitybotting.security.acuity.aws.secrets.AwsSecretService;
import com.acuitybotting.db.arango.security.Credentials;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDB.Builder;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.AbstractArangoConfiguration;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableArangoRepositories(basePackages = { "com.acuitybotting.db.arango.path_finding" })
public class ArangoDBConfigPathFinding extends AbstractArangoConfiguration{

    private final AwsSecretService secretService;

    @Autowired
    public ArangoDBConfigPathFinding(AwsSecretService secretService) {
        this.secretService = secretService;
    }

    @Override
    public Builder arango() {
        Credentials credentials = secretService.getSecret("secretsmanager.us-east-1.amazonaws.com", "us-east-1", "DbCredentials", Credentials.class).orElseThrow(() -> new RuntimeException("Failed to access Arango secret."));
        return new ArangoDB.Builder()
                .host(credentials.getArangoHost(), Integer.parseInt(credentials.getArangoPort()))
                .maxConnections(20)
                .user(credentials.getArangoUsername())
                .password(credentials.getArangoPassword());
    }

    @Override
    public String database() {
        return "AcuityBotting";
    }
}
