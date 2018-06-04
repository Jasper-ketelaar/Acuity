package com.acuitybotting.aws.security.cognito;

import com.acuitybotting.aws.security.cognito.domain.CognitoConfig;
import com.acuitybotting.aws.security.cognito.domain.CognitoLoginResult;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeResult;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/4/2018.
 */
@Service
public class CognitoService {

    public Optional<CognitoLoginResult> login(CognitoConfig cognitoConfig, String username, String password){
        try {
            CognitoHelper helper = helper(cognitoConfig);
            RespondToAuthChallengeResult result = helper.validateUser(username, password);
            if (result != null){
                String resultString = result.getAuthenticationResult().getIdToken();
                Map<String, Object> payload = CognitoJWTParser.getPayload(resultString);
                return Optional.of(CognitoLoginResult.builder()
                        .payload(payload)
                        .result(resultString)
                        .build());
            }
        }
        catch (Exception e){
            throw new RuntimeException("Exception during login.", e);
        }

        return Optional.empty();
    }

    public Optional<Credentials> getCredentials(CognitoConfig cognitoConfig, CognitoLoginResult loginResult){
        try {
            CognitoHelper helper = helper(cognitoConfig);
            String provider = loginResult.getPayload().get("iss").toString().replace("https://", "");
            return Optional.ofNullable(helper.getCredentials(provider, loginResult.getResult()));
        }
        catch (Exception e){
            throw new RuntimeException("Exception during credential acquisition.", e);
        }
    }

    private CognitoHelper helper(CognitoConfig cognitoConfig){
        return new CognitoHelper(cognitoConfig.getPoolId(), cognitoConfig.getClientappId(), cognitoConfig.getFedPoolId(), cognitoConfig.getCustomDomain(), cognitoConfig.getRegion(), cognitoConfig.getRedirectUrl(), cognitoConfig.getSecretKey());
    }
}
