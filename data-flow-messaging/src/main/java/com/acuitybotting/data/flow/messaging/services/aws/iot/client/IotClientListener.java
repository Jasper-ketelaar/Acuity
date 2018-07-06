package com.acuitybotting.data.flow.messaging.services.aws.iot.client;

/**
 * Created by Zachary Herridge on 7/6/2018.
 */
public interface IotClientListener {

    void onConnect();

    void onConnectionFailure();

    void onConnectionClosed();

}
