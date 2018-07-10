package com.acuitybotting.security.rabbitmq.service.handler;

import com.acuitybotting.security.rabbitmq.domain.LoginResult;
import com.acuitybotting.security.rabbitmq.domain.ResourcePermission;
import com.acuitybotting.security.rabbitmq.domain.ResourceType;

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