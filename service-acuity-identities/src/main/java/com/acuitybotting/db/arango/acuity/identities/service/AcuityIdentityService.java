package com.acuitybotting.db.arango.acuity.identities.service;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityIdentity;
import com.acuitybotting.db.arango.acuity.identities.repositories.AcuityIdentityRepository;
import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/14/2018.
 */
@Service
public class AcuityIdentityService {

    private final AcuityIdentityRepository identityRepository;

    @Autowired
    public AcuityIdentityService(AcuityIdentityRepository identityRepository) {
        this.identityRepository = identityRepository;
    }

    public void createIfAbsent(AcuityPrincipal acuityPrincipal){
        String key = acuityPrincipal.getKey();
        if (!identityRepository.findByPrincipalKeysContaining(key).isPresent()){
            AcuityIdentity acuityIdentity = new AcuityIdentity();
            acuityIdentity.setEmail(acuityPrincipal.getEmail());
            acuityIdentity.setPrincipalKeys(new String[]{key});
            identityRepository.save(acuityIdentity);
        }
    }
}
