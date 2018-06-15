package com.acuitybotting.website.acuity.security;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityIdentityService;
import com.acuitybotting.security.acuity.spring.AcuityPrincipalContext;
import com.vaadin.server.VaadinSession;

import java.util.HashMap;
import java.util.Map;
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

    public static <T> Optional<T> getProperty(String key, Class<T> tClass){
        return getProperty(key, tClass, true);
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getProperty(String key, Class<T> tClass, boolean cached){
        return getCurrent(cached).map(AcuityIdentity::getProperties).map(stringObjectMap -> (T) stringObjectMap.get(key));
    }

    public static boolean putProperty(String key, Object value){
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) return false;
        AcuityIdentityService service = session.getAttribute(AcuityIdentityService.class);
        if (service == null) return false;
        String principalKey = AcuityPrincipalContext.getPrincipalKey();
        if (principalKey == null) return false;
        AcuityIdentity acuityIdentity = service.getIdentityRepository().findByPrincipalKeysContaining(principalKey).orElse(null);

        Map<String, Object> properties = acuityIdentity.getProperties();
        if (properties == null) properties = new HashMap<>();
        properties.put(key, value);
        session.setAttribute(AcuityIdentity.class, service.getIdentityRepository().save(acuityIdentity));
        return true;
    }
}
