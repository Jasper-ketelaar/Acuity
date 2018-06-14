package com.acuitybotting.security.acuity.jwt;

import com.acuitybotting.security.acuity.jwt.domain.AcuityPrincipal;
import com.acuitybotting.security.acuity.jwt.domain.JwtKey;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/5/2018.
 */
@Service
public class AcuityJwtService {

    public Optional<AcuityPrincipal> getPrincipal(String token){
        return decodeAndVerify(token).map(decodedJWT -> {
           AcuityPrincipal acuityPrincipal = new AcuityPrincipal();
            acuityPrincipal.setUsername(decodedJWT.getClaim("cognito:username").asString());
            acuityPrincipal.setSub(decodedJWT.getClaim("sub").asString());
            acuityPrincipal.setRealm(decodedJWT.getClaim("iss").asString());
            acuityPrincipal.setEmail(decodedJWT.getClaim("email").asString());
            acuityPrincipal.setRoles(decodedJWT.getClaim("cognito:roles").asArray(String.class));
           return acuityPrincipal;
        });
    }

    public Optional<DecodedJWT> decodeAndVerify(String token){
        if (token == null) return Optional.empty();
        try {
            JWTVerifier verifier = JWT.require(getRSA256()).build();
            return Optional.ofNullable(verifier.verify(token));
        }
        catch (Exception exception){
            exception.printStackTrace();
        }

        return Optional.empty();
    }

    private JwtKey[] getKeys(){
        return new Gson().fromJson("[{\"alg\":\"RS256\",\"e\":\"AQAB\",\"kid\":\"HN4/0I1Ffyrht3pIv5ykoAB1p3Z+TUvJ4tkr4pgsKPg=\",\"kty\":\"RSA\",\"n\":\"6sa1sS-RMw9EmkbRaECyuQSa9t8CrBdyfTCfPXZnokcME9CNEKlMAkQsG5fC8GMPWJxJnHtPvJgPJqr_iHeabEGuAxyH1vDT-9nCfgYr36j-8HjaWL6zO_wga_BIG9mS_ZZdBWCqHqVhBTrYIHZzYBWIT4qRSZYL9Nqbr1VKn4DvqXlSAcmmt-yRejkg08tApNyMD8GAOekRuQ50BodrFgN7ULVAYPwJeFdHkgg4Dp8VvMj6OHq05H6mwEP3cN0HI7Tx8341h2zowiKSHntNV5UGQl6IMcXVeQh7bxxSq15Vk0rNXDi1WgofEJzE9rDl_gYUGUQCirN7CiekQ020sQ\",\"use\":\"sig\"},{\"alg\":\"RS256\",\"e\":\"AQAB\",\"kid\":\"OuN3/nwMmQaO59Cm62yyDXFuFuEIy/LjVd+t1Hbg96Q=\",\"kty\":\"RSA\",\"n\":\"oTRR9RLdR47NM5fn1J9UlcX3SWJuMv4WtEFPetf8LXdqvkaffAJ09QzNi9BKOFk6rbxszkEML0CmgSkVHEz7E46sp3yHwvDHOMyQfYzitrDGeeAEoDNema5Gh2odyddF-aagwROgbrDSoX9NGj6QzCHcRG9vdCTNd_M7J2Iv7U0J5pVsQIukaXEW0_L8_1Bvyrik9hEH7NYsUfSc0a3-YpOLJJCxUkVR1ZJvUt74ygaFoWPCvKu-64XREMneuarVs6lh7unoc5BfpwWtfJZJbxhZf7TtwVoYW4LugrSp88aYSkn3iMtIsucHf0cH1Een7TU_u5WOJBgWQMnFiR8iGQ\",\"use\":\"sig\"},{\"alg\":\"RS256\",\"e\":\"AQAB\",\"kid\":\"Opc3/kFP2VfrYAdRQXcPunRiviLjK76He1dmAGkUG3o=\",\"kty\":\"RSA\",\"n\":\"qKl3twVrmzY3-Bw0RasyrKYTouTcRJQ0yZ1iAemvnafzC4SoWRNbwVcG3VqOrC17_3vJy3Ft7Y-K9rHpuDLi3-LvA_vP7A-tAPTF3Ts4DMDJ5ekEkjHqs_z4avxXnjuhLzQ8A3N5kaVDWFHguJKAL9TnCRiLaUBM5UTj4R6pqJudH6uUFObHXU2Hn9NUWyci6thQOG9QXPYVWG052NDexZydn18g9vddJPRa4s0uyT7pUymp_gQpUV33HZ0_z2YQpkIrnrz-xJZey1vwSzsy_-if69SBJKvX_IL-8L21ppPVV_SdJ08SGKpkzjyKe4sEYbvHjENYF98LBmNZ4VSq4w\",\"use\":\"sig\"},{\"alg\":\"RS256\",\"e\":\"AQAB\",\"kid\":\"KRXf2caMiRilyujw1NIgiN2LzHdOOK1lkqNr42Q+YFc=\",\"kty\":\"RSA\",\"n\":\"ooJcxlHDWPpU4irE9MjKXbd0ptIXysgzYq7757ciQCtiY50bm2sqHDWDCJgTzynQao0NPgrj0ty1qJ3Raii2JgAdesCUJXEjh1Ezv1RqCAgppbdyVt4bEJhxWbNbYyWk2VgE-v81TPzKaLBtv16YB7qhg_4aGgmoKPSvZOYJCW3uv9LqTkQ_nGRsJhLp2hEf5tEOJT-KdRt7baopaikVxqExeDn39ic7ojMORDP3IpgSLpqvnHwsnj7nH317Y0Id8oqoBhNYPTVtI3kcHyBFNhTj_sKWQlARodOAnv_gk0a5358MCJ2s9CXFORHGAKYLo12m8pFA4TUUOtwe5kNYiw\",\"use\":\"sig\"}]", JwtKey[].class);
    }

    private JwtKey getKey(String kid){
        for (JwtKey jwtKey : getKeys()) {
            if (jwtKey.getKid().equals(kid)) return jwtKey;
        }
        return null;
    }

    private PublicKey getPublicKey(String kid) throws InvalidKeySpecException, NoSuchAlgorithmException {
        JwtKey key = getKey(kid);
        if (key == null) return null;

        byte[] decodedModulus = Base64.getUrlDecoder().decode(key.getN());
        byte[] decodedExponent = Base64.getUrlDecoder().decode(key.getE());

        BigInteger modulus = new BigInteger(1, decodedModulus);
        BigInteger exponent = new BigInteger(1, decodedExponent);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicKeySpec);
    }

    private Algorithm getRSA256(){
        return Algorithm.RSA256(new RSAKeyProvider() {
            @Override
            public RSAPublicKey getPublicKeyById(String s) {
                try {
                    return (RSAPublicKey) getPublicKey(s);
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public RSAPrivateKey getPrivateKey() {
                return null;
            }

            @Override
            public String getPrivateKeyId() {
                return null;
            }
        });
    }
}
