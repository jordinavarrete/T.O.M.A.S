package com.puchdemont.tomas;


// THIS IS THE PHONE THAT RECEIVES UPDATES

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
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
import java.util.Random;

public class NearbyServer {

    public static class ConnectionData
    {
        public static final String ServiceId = "com.puchdemont.tomas.Padalustro";
    }

    public static class Helper
    {
        public static boolean Running = false;
        static ConnectionsClient connectionsClient;
        static ConnectionLifecycleCallback connectionLifecycleCallback;
        static MainActivity Activity;
        public static void Initialize(MainActivity activity)
        {
            if(Running) return;


            boolean isBtOn = BluetoothAdapter.getDefaultAdapter().isEnabled();
            LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            boolean isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!isBtOn || !isLocationEnabled) {
                Log.e("ServerReceiver", "Bluetooth or location is not enabled");
                return;
            }


            Running = true;
            connectionsClient = Nearby.getConnectionsClient(activity);
            Activity = activity;
            PayloadCallback payloadCallback = new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    String received = new String(payload.asBytes(), StandardCharsets.UTF_8);
                    Log.e("ServerReceiver", "Received: " + received);
                    Activity.LoadDataFromString(received);
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) { }
            };


            connectionLifecycleCallback = new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    // Automatically accept the connection
                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Log.d("ServerReceiver", "Connected to client: " + endpointId);
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.d("ServerReceiver", "Disconnected: " + endpointId);
                }
            };

            StartAdvertising();
        }

        public static void StartAdvertising()
        {
            connectionsClient.stopAllEndpoints();
            connectionsClient.startAdvertising(
                    new String(new byte[]{97,98,99,100}, StandardCharsets.UTF_8), // your endpoint name
                    ConnectionData.ServiceId, // your service ID
                    connectionLifecycleCallback,
                    new AdvertisingOptions.Builder()
                            .setStrategy(Strategy.P2P_STAR)
                            .build()
            );
        }

        public static void StopAdvertising()
        {
            connectionsClient.stopAdvertising();
            Running = false;
        }
    }
}
