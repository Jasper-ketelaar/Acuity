package com.acuitybotting.security.acuity.aws.cognito.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Zachary Herridge on 6/4/2018.
 */
@Data
@Builder
public class CognitoConfiguration {

    private String poolId;
    private String clientAppId;
    private String fedPoolId;
    private String customDomain;
    private String region;
    private String redirectUrl;
    private String secretKey;

}
