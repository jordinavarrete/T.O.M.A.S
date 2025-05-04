package com.puchdemont.tomas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentOnAttachListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class BluetoothClient {
    public static class Helper {
        private static boolean discoveryStopped = false;

        private final static String LOG_KEY = "BluetoothClient";

        static MainActivity Activity;
        private static BluetoothAdapter bluetoothAdapter;

        @SuppressLint("MissingPermission")
        public static void Connect(MainActivity activity) {
            discoveryStopped = false;
            Activity = activity;
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            activity.registerReceiver(receiver, filter);
            Log.d(LOG_KEY, "Enabling device discovery...");
            bluetoothAdapter.startDiscovery();

        }

        @SuppressLint("MissingPermission")
        private static void _connectTo(BluetoothDevice device)
        {
            Log.e(LOG_KEY, "Connecting to " + device.getAddress() + "...");
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                // Bluetooth is not available or not enabled
                return;
            }

            BluetoothSocket socket = null;

            try {
                // Use a well-known UUID for SPP (Serial Port Profile)
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                Log.d(LOG_KEY, "Attempting to get socket from UUID...");
                socket = device.createRfcommSocketToServiceRecord(uuid);
                Log.d(LOG_KEY, "Attempting to connect to socket...");
                socket.connect();
                ReadFromSocket(socket);

                // Connection successful
            } catch (IOException e) {
                Log.e(LOG_KEY, "An error occurred while connecting to server socket");
                e.printStackTrace();
                // Handle connection failure
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException closeException) {
                        closeException.printStackTrace();
                    }
                }
            }
        }

        private static void ReadFromSocket(BluetoothSocket socket)
        {
            try {
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                String result = "";
                Log.d(LOG_KEY, "Begin readline of socket...");
                try {
                    while ((line = reader.readLine()) != null) {
                        // Process the received data
                        result += line + "\n";
                    }
                } catch (IOException e) {
                    Log.d(LOG_KEY, "IOException thrown, socket probably closed and read can be considered finished");
                    String finalResult = result;
                    Activity.runOnUiThread(() -> Activity.LoadDataFromString(finalResult));
                    return;
                }
                Connect(Activity);
            } catch (Exception ex)
            {
                Log.d(LOG_KEY, "Reading from socket failed");
                ex.printStackTrace();
                Connect(Activity);
            }
            finally
            {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        private static final BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String name = null;
                    String currentName = null;
                    try {
                        name = device.getName();
                        currentName = bluetoothAdapter.getName();
                        Log.d(LOG_KEY, "Found device: " + name + " - " + device.getAddress());
                    } catch (SecurityException ex)
                    {
                        ex.printStackTrace();
                    }
                    if(name != null && name.startsWith("TOMASNET") && !name.equals(currentName))
                    {
                        _connectTo(device);
                        try {
                            bluetoothAdapter.cancelDiscovery();
                        } catch (SecurityException ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        };


        @SuppressLint("MissingPermission")
        public static void onDestroy() {
            try {
                bluetoothAdapter.cancelDiscovery();
                Activity.unregisterReceiver(receiver);
            } catch (Exception ex)
            {

            }
        }
    }
}
