package com.acuitybotting.security.acuity.jwt.domain;


import lombok.Data;

/**
 * Created by Zachary Herridge on 6/6/2018.
 */
@Data
public class AcuityPrincipal  {

    private String username;
    private String email;
    private String sub;
    private String realm;
    private String[] roles;

}
