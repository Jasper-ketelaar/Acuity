package com.acuitybotting.db.arango.security;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Credentials {

    private String host;
    private String username;
    private String password;
    private String port;
}
