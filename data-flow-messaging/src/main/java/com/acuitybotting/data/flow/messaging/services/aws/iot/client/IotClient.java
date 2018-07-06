package com.acuitybotting.data.flow.messaging.services.aws.iot.client;

import com.amazonaws.services.iot.client.AWSIotMqttClient;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zachary Herridge on 7/6/2018.
 */
public class IotClient extends AWSIotMqttClient {

    private List<IotClientListener> listeners = new ArrayList<>();

    public IotClient(String clientEndpoint, String clientId, KeyStore keyStore, String keyPassword) {
        super(clientEndpoint, clientId, keyStore, keyPassword);
    }

    public IotClient(String clientEndpoint, String clientId, String awsAccessKeyId, String awsSecretAccessKey) {
        super(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey);
    }

    public IotClient(String clientEndpoint, String clientId, String awsAccessKeyId, String awsSecretAccessKey, String sessionToken) {
        super(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey, sessionToken);
    }

    @Override
    public void onConnectionSuccess() {
        super.onConnectionSuccess();
        for (IotClientListener listener : listeners) {
            listener.onConnect();
        }
    }

    @Override
    public void onConnectionFailure() {
        super.onConnectionFailure();
        for (IotClientListener listener : listeners) {
            listener.onConnectionFailure();
        }
    }

    @Override
    public void onConnectionClosed() {
        super.onConnectionClosed();
        for (IotClientListener listener : listeners) {
            listener.onConnectionClosed();
        }
    }

    public List<IotClientListener> getListeners() {
        return listeners;
    }
}
