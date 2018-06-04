package com.acuitybotting.aws.security.cognito.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Zachary Herridge on 6/4/2018.
 */
@Data
@Builder
public class CognitoConfig {

    private String poolId;
    private String clientappId;
    private String fedPoolId;
    private String customDomain;
    private String region;
    private String redirectUrl;
    private String secretKey;

}
