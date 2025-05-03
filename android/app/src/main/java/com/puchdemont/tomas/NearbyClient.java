package com.puchdemont.tomas;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.nio.charset.StandardCharsets;

public class NearbyClient {
    public static class Helper
    {
        private static boolean con = false;
        private static boolean run = false;
        private static ConnectionsClient connectionsClient;
        private static EndpointDiscoveryCallback endpointDiscoveryCallback;
        private static ConnectionLifecycleCallback connectionLifecycleCallback;
        private static PayloadCallback payloadCallback;
        private static String connectedId;

        public static void initialise(Context context) {
            if(run) return;
            connectionsClient = Nearby.getConnectionsClient(context);

            payloadCallback = new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    String msg = new String(payload.asBytes(), StandardCharsets.UTF_8);
                    Log.d("Client", "Received from server: " + msg);
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) { }
            };

            connectionLifecycleCallback = new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        connectionsClient.stopDiscovery();
                        connectedId = endpointId;
                        sendPayload("Hello from client");
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.i("KIG", "DISCONNECTED");
                }
            };



            endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    connectionsClient.requestConnection(
                            "ClientName",
                            endpointId,
                            connectionLifecycleCallback
                    );
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    connectedId = null;
                }
            };

            connectionsClient.startDiscovery(
                    NearbyServer.ConnectionData.ServiceId,
                    endpointDiscoveryCallback,
                    new DiscoveryOptions.Builder()
                            .setStrategy(Strategy.P2P_STAR)
                            .build()
            );
            run = true;
        }

        public static void sendPayload(String payload) {
            connectionsClient.sendPayload(connectedId,
                    Payload.fromBytes(payload.getBytes(StandardCharsets.UTF_8)));
        }

    }
}
