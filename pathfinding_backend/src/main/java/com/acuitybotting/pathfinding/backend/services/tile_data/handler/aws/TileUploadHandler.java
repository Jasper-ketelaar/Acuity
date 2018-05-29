package com.acuitybotting.pathfinding.backend.services.tile_data.handler.aws;

import com.acuitybotting.pathfinding.backend.services.tile_data.domain.TileUpload;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.amazonaws.util.Base64;
import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;


/**
 * Created by Zachary Herridge on 5/29/2018.
 */
public class TileUploadHandler implements RequestHandler<TileUpload, String> {

    private final Gson gson = new Gson();

    @Override
    public String handleRequest(TileUpload tileUpload, Context context) {
        String json = gson.toJson(tileUpload);
        return "Sec: " + decryptKey();
    }

    private static String decryptKey() {
        String secretName = "MongoAcess";
        String endpoint = "secretsmanager.us-east-1.amazonaws.com";
        String region = "us-east-1";

        AwsClientBuilder.EndpointConfiguration config = new AwsClientBuilder.EndpointConfiguration(endpoint, region);
        AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
        clientBuilder.setEndpointConfiguration(config);
        AWSSecretsManager client = clientBuilder.build();

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
        GetSecretValueResult getSecretValueResponse = null;
        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch(ResourceNotFoundException e) {
            System.out.println("The requested secret " + secretName + " was not found");
        } catch (InvalidRequestException e) {
            System.out.println("The request was invalid due to: " + e.getMessage());
        } catch (InvalidParameterException e) {
            System.out.println("The request had invalid params: " + e.getMessage());
        }

        if(getSecretValueResponse == null) {
            return null;
        }

        return getSecretValueResponse.getSecretString();
    }
}
