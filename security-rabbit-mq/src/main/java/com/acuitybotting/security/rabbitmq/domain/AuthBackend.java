package com.acuitybotting.security.rabbitmq.domain;

public interface AuthBackend {
    LoginResult login(String username);

    LoginResult login(String username,
                      String password);

    boolean checkVhost(String username,
                       String vhost);

    boolean checkResource(String username,
                          String vhost,
                          String resourceName,
                          ResourceType resourceType,
                          ResourcePermission permission);

    boolean checkTopic(String username,
                       String vhost,
                       String resourceName,
                       ResourceType resourceType,
                       ResourcePermission permission,
                       String routingKey);
}