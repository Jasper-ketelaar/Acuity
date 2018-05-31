package com.acuitybotting.db.arango.security;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Credentials {

    private String arangoHost;
    private String arangoUsername;
    private String arangoPassword;
    private String arangoPort;
}
