package com.puchdemont.tomas;


// THIS IS THE PHONE THAT RECEIVES UPDATES

import android.content.Context;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.nio.charset.StandardCharsets;

public class NearbyServer {

    public static class ConnectionData
    {
        public static final String ServiceId = "com.puchdemont.tomas.Padalustro";
    }

    public static class Helper
    {
        static ConnectionsClient connectionsClient;
        static ConnectionLifecycleCallback connectionLifecycleCallback;
        public void Initialize(Context context)
        {
            connectionsClient = Nearby.getConnectionsClient(context);
            connectionLifecycleCallback = new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    // Automatically accept the connection
                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Log.d("Server", "Connected to client: " + endpointId);
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.d("Server", "Disconnected: " + endpointId);
                }
            };

            PayloadCallback payloadCallback = new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    String received = new String(payload.asBytes(), StandardCharsets.UTF_8);
                    Log.d("Server", "Received: " + received);
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) { }
            };

        }

        public void StartAdvertising()
        {
            connectionsClient.startAdvertising(
                    "TOMAS", // your endpoint name
                    "ILoveTomas", // your service ID
                    connectionLifecycleCallback,
                    new AdvertisingOptions.Builder()
                            .setStrategy(Strategy.P2P_STAR)
                            .build()
            );
        }



    }
}
