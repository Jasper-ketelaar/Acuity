package com.acuitybotting.security.acuity.aws.cognito.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Zachary Herridge on 6/4/2018.
 */
@Data
@Builder
public class CognitoTokens {

    private String idToken;
    private String accessToken;
    private String refreshToken;

}
