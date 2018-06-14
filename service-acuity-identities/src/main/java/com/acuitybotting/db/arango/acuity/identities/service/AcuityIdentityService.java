package com.acuitybotting.db.arango.acuity.identities.service;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.acuitybotting.db.arango.acuity.identities.repositories.AcuityIdentityRepository;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/14/2018.
 */
@Service
@Data
public class AcuityIdentityService {

    private final AcuityIdentityRepository identityRepository;

    @Autowired
    public AcuityIdentityService(AcuityIdentityRepository identityRepository) {
        this.identityRepository = identityRepository;
    }

    public AcuityIdentity createIfAbsent(AcuityPrincipal acuityPrincipal){
        String key = acuityPrincipal.getKey();
        AcuityIdentity acuityIdentity = identityRepository.findByPrincipalKeysContaining(key).orElse(null);

        if (acuityIdentity == null){
            long now = System.currentTimeMillis();
            acuityIdentity = new AcuityIdentity();
            acuityIdentity.setEmail(acuityPrincipal.getEmail());
            acuityIdentity.setPrincipalKeys(new String[]{key});
            acuityIdentity.setLastSignInTime(now);
            acuityIdentity.setCreationTime(now);
            return identityRepository.save(acuityIdentity);
        }

        return acuityIdentity;
    }

    public void updateLoginFields(AcuityIdentity acuityIdentity) {
        acuityIdentity.setLastSignInTime(System.currentTimeMillis());
        identityRepository.save(acuityIdentity);
    }
}
