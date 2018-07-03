package com.acuitybotting.db.arango;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDB.Builder;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.AbstractArangoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableArangoRepositories(basePackages = { "com.acuitybotting.db.arango.path_finding" })
public class ArangoDBConfigPathFinding extends AbstractArangoConfiguration{

    @Value("${arango.host}")
    private String host;

    @Value("${arango.port}")
    private int port;

    @Value("${arango.username}")
    private String username;

    @Value("${arango.password}")
    private String password;

    @Override
    public Builder arango() {
        return new ArangoDB.Builder()
                .host(host, port)
                .maxConnections(20)
                .user(username)
                .password(password);
    }

    @Override
    public String database() {
        return "AcuityBotting";
    }
}
