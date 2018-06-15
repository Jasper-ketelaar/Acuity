package com.acuitybotting.website.acuity.security;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityIdentityService;
import com.acuitybotting.security.acuity.spring.AcuityPrincipalContext;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/15/2018.
 */
public class AcuityIdentityContext {

    public static Optional<AcuityIdentity> getCurrent(){
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) return Optional.empty();
        AcuityIdentityService service = session.getAttribute(AcuityIdentityService.class);
        if (service == null) return Optional.empty();
        String principalKey = AcuityPrincipalContext.getPrincipalKey();
        if (principalKey == null) return Optional.empty();
        Optional<AcuityIdentity> acuityIdentity = service.getIdentityRepository().findByPrincipalKeysContaining(principalKey);
        session.setAttribute(AcuityIdentity.class, acuityIdentity.orElse(null));
        return acuityIdentity;
    }

    public static Optional<AcuityIdentity> getCurrent(boolean cached){
        if (!cached) return getCurrent();

        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) return Optional.empty();
        AcuityIdentity acuityIdentity = session.getAttribute(AcuityIdentity.class);
        if (acuityIdentity != null) return Optional.of(acuityIdentity);
        return getCurrent();
    }

    public static String getIdNullSafe(){
        return getIdNullSafe(true);
    }

    public static String getIdNullSafe(boolean cached){
        return getCurrent(cached).map(AcuityIdentity::getId).orElse("NULL");
    }

    public static boolean isCurrent(AcuityIdentity identity){
        return isCurrent(identity, true);
    }

    public static boolean isCurrent(AcuityIdentity identity, boolean cached){
        if (identity == null) return false;
        return getIdNullSafe(cached).equals(identity.getId());
    }

    public static boolean isLoggedIn() {
        return AcuityPrincipalContext.getPrincipal().isPresent();
    }
}
