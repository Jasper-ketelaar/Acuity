package com.acuitybotting.db.arango.security;

import lombok.Data;

@Data
public class Credentials {

    private String arangoHost;
    private String arangoUsername;
    private String arangoPassword;
    private String arangoPort;
}
