package com.acuitybotting.website.acuity.security;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityIdentityService;
import com.acuitybotting.security.acuity.spring.AcuitySecurityContext;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/15/2018.
 */
public class AcuityIdentityContext {

    public static Optional<AcuityIdentity> getCurrent(){
        VaadinSession session = UI.getCurrent().getSession();
        if (session == null) return Optional.empty();
        AcuityIdentityService service = session.getAttribute(AcuityIdentityService.class);
        if (service == null) return Optional.empty();
        String principalKey = AcuitySecurityContext.getPrincipalKey();
        if (principalKey == null) return Optional.empty();
        Optional<AcuityIdentity> acuityIdentity = service.getIdentityRepository().findByPrincipalKeysContaining(principalKey);
        session.setAttribute(AcuityIdentity.class, acuityIdentity.orElse(null));
        return acuityIdentity;
    }

    public static Optional<AcuityIdentity> getCachedOrUpdate(){
        VaadinSession session = UI.getCurrent().getSession();
        if (session == null) return Optional.empty();
        AcuityIdentity acuityIdentity = session.getAttribute(AcuityIdentity.class);
        if (acuityIdentity != null) return Optional.of(acuityIdentity);
        return getCurrent();
    }

    public static boolean isLoggedIn() {
        return AcuitySecurityContext.getPrincipal().isPresent();
    }
}
