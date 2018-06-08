package com.acuitybotting.security.acuity.aws.secrets.domain;

import lombok.Data;

/**
 * Created by Zachary Herridge on 6/8/2018.
 */
@Data
public class AccessKeyCredentials {

    private String region;
    private String accessKey;
    private String secretKey;

}
