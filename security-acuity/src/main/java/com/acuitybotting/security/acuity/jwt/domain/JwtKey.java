package com.acuitybotting.security.acuity.jwt.domain;

import lombok.Data;

/**
 * Created by Zachary Herridge on 6/5/2018.
 */
@Data
public class JwtKey {
    private String alg;
    private String e;
    private String kid;
    private String kty;
    private String n;
    private String use;
}
