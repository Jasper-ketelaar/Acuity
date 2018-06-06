package com.acuitybotting.security.acuity.jwt;

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
        return new Gson().fromJson("[{\"alg\":\"RS256\",\"e\":\"AQAB\",\"kid\":\"Opc3/kFP2VfrYAdRQXcPunRiviLjK76He1dmAGkUG3o=\",\"kty\":\"RSA\",\"n\":\"qKl3twVrmzY3-Bw0RasyrKYTouTcRJQ0yZ1iAemvnafzC4SoWRNbwVcG3VqOrC17_3vJy3Ft7Y-K9rHpuDLi3-LvA_vP7A-tAPTF3Ts4DMDJ5ekEkjHqs_z4avxXnjuhLzQ8A3N5kaVDWFHguJKAL9TnCRiLaUBM5UTj4R6pqJudH6uUFObHXU2Hn9NUWyci6thQOG9QXPYVWG052NDexZydn18g9vddJPRa4s0uyT7pUymp_gQpUV33HZ0_z2YQpkIrnrz-xJZey1vwSzsy_-if69SBJKvX_IL-8L21ppPVV_SdJ08SGKpkzjyKe4sEYbvHjENYF98LBmNZ4VSq4w\",\"use\":\"sig\"},{\"alg\":\"RS256\",\"e\":\"AQAB\",\"kid\":\"KRXf2caMiRilyujw1NIgiN2LzHdOOK1lkqNr42Q+YFc=\",\"kty\":\"RSA\",\"n\":\"ooJcxlHDWPpU4irE9MjKXbd0ptIXysgzYq7757ciQCtiY50bm2sqHDWDCJgTzynQao0NPgrj0ty1qJ3Raii2JgAdesCUJXEjh1Ezv1RqCAgppbdyVt4bEJhxWbNbYyWk2VgE-v81TPzKaLBtv16YB7qhg_4aGgmoKPSvZOYJCW3uv9LqTkQ_nGRsJhLp2hEf5tEOJT-KdRt7baopaikVxqExeDn39ic7ojMORDP3IpgSLpqvnHwsnj7nH317Y0Id8oqoBhNYPTVtI3kcHyBFNhTj_sKWQlARodOAnv_gk0a5358MCJ2s9CXFORHGAKYLo12m8pFA4TUUOtwe5kNYiw\",\"use\":\"sig\"}]", JwtKey[].class);
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
