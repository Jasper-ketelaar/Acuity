package com.acuitybotting.security.rabbitmq.domain;

import com.acuitybotting.security.acuity.jwt.AcuityJwtService;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class AuthBackendImpl implements AuthBackend {

    private static final LoginResult ACCEPTED = new LoginResult(true, new String[0]);
    private static final LoginResult REFUSED = new LoginResult(false, new String[0]);

    private AcuityJwtService acuityJwtService;

    public AuthBackendImpl(AcuityJwtService acuityJwtService) {
        this.acuityJwtService = acuityJwtService;
    }

    @Override
    public LoginResult login(String username) {
        log.info("Login request by {}.", username);
        return REFUSED;
    }

    @Override
    public LoginResult login(String username, String password) {
        log.info("Login request by {} with password length {}.", username, password.length());

        AcuityPrincipal acuityPrincipal = acuityJwtService.getPrincipal(password).orElse(null);
        if (acuityPrincipal == null) return REFUSED;
        if (username.equals(acuityPrincipal.getKey())) return ACCEPTED;
        return REFUSED;
    }

    @Override
    public boolean checkVhost(String username, String vhost) {
        log.info("VHost check {} {}.", username, vhost);
        return "AcuityBotting".equals(vhost);
    }

    @Override
    public boolean checkResource(String username, String vhost, String resourceName, ResourceType resourceType, ResourcePermission permission) {
        log.info("Resource check {} {} {} {} {}.", username, vhost, resourceName, resourceType, permission);
        return resourceType == ResourceType.QUEUE && resourceName.startsWith("user." + username + ".queue.");
    }

    @Override
    public boolean checkTopic(String username, String vhost, String resourceName, ResourceType resourceType, ResourcePermission permission, String routingKey) {
        log.info("Topic check {} {} {} {} {} {}.", username, vhost, resourceName, resourceType, permission, routingKey);
        return false;
    }
}