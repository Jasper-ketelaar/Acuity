package com.acuitybotting.aws.security.cognito;

import com.acuitybotting.aws.security.cognito.domain.CognitoConfig;
import com.acuitybotting.aws.security.cognito.domain.CognitoLoginResult;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/4/2018.
 */
@Service
public class CognitoService {

    public Optional<CognitoLoginResult> login(CognitoConfig cognitoConfig, String username, String password){
        CognitoHelper helper = helper(cognitoConfig);

        String result = helper.validateUser(username, password);
        if (result != null){
            Map<String, Object> payload = CognitoJWTParser.getPayload(result);
            return Optional.of(CognitoLoginResult.builder()
                    .payload(payload)
                    .result(result)
                    .build());
        }
        return Optional.empty();
    }

    public Optional<Credentials> getCredentials(CognitoConfig cognitoConfig, CognitoLoginResult loginResult){
        CognitoHelper helper = helper(cognitoConfig);
        String provider = loginResult.getPayload().get("iss").toString().replace("https://", "");
        return Optional.ofNullable(helper.getCredentials(provider, loginResult.getResult()));
    }

    private CognitoHelper helper(CognitoConfig cognitoConfig){
        return new CognitoHelper(cognitoConfig.getPoolId(), cognitoConfig.getClientappId(), cognitoConfig.getFedPoolId(), cognitoConfig.getCustomDomain(), cognitoConfig.getRegion(), cognitoConfig.getRedirectUrl(), cognitoConfig.getSecretKey());
    }
}
