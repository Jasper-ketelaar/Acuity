package com.acuitybotting.db.arango;

import com.arangodb.ArangoDB.Builder;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.AbstractArangoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:arango.credentials")
@EnableArangoRepositories(basePackages = {"com.acuitybotting.db.arango.acuity"})
public class ArangoDBConfigAcuity extends AbstractArangoConfiguration {

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
        return new Builder().host(host, port).user(username).password(password);
    }

    @Override
    public String database() {
        return "AcuityBotting";
    }
}
