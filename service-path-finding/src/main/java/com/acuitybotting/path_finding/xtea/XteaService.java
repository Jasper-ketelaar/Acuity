package com.acuitybotting.path_finding.xtea;

import com.acuitybotting.db.arango.path_finding.repositories.xtea.XteaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Zachary Herridge on 6/22/2018.
 */
@Service
public class XteaService {

    private final XteaRepository xteaRepository;

    @Autowired
    public XteaService(XteaRepository xteaRepository) {
        this.xteaRepository = xteaRepository;
    }
}
