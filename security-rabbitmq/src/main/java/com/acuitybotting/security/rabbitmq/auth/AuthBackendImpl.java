package com.acuitybotting.security.rabbitmq.auth;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthBackendImpl implements AuthBackend {

    private static final LoginResult ACCEPTED = new LoginResult(true, new String[]{"administrator"});
    private static final LoginResult REFUSED = new LoginResult(false);

    @Override
    public LoginResult login(String username) {
        log.info("Login request by {}.", username);
        return ACCEPTED;
    }

    @Override
    public LoginResult login(String username, String password) {
        log.info("Login request by {} with password length {}.", username, password.length());
        return ACCEPTED;
    }

    @Override
    public boolean checkVhost(String username, String vhost) {
        log.info("VHost check {} {}.", username, vhost);
        return true;
    }

    @Override
    public boolean checkResource(String username, String vhost, String resourceName, ResourceType resourceType, ResourcePermission permission) {
        log.info("Resource check {} {} {} {} {}.", username, vhost, resourceName, resourceType, permission);
        return true;
    }

    @Override
    public boolean checkTopic(String username, String vhost, String resourceName, ResourceType resourceType, ResourcePermission permission, String routingKey) {
        log.info("Topic check {} {} {} {} {} {}.", username, vhost, resourceName, resourceType, permission, routingKey);
        return true;
    }
}
