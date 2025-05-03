package com.puchdemont.tomas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;

public class BluetoothServer {
    public static class Helper {
        private static final String LOG_KEY = "BluetoothServer";

        private static BluetoothServerSocket mmServerSocket;
        private static MainActivity Activity;
        private static String oldName = "Device";
        private static BluetoothAdapter adapter;
        public static void InitializeAndServe(MainActivity activity) {
            Activity = activity;
            adapter = BluetoothAdapter.getDefaultAdapter();
            Serve();
        }

        @SuppressLint("MissingPermission")
        private static void RequestDeviceExposure()
        {
            try {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); // 300 seconds
                Activity.startActivity(discoverableIntent);
            } catch (Exception ex)
            {
                Log.e(LOG_KEY, "Could not request device exposure");
                ex.printStackTrace();
            }
        }

        public static void Serve()
        {
            RequestDeviceExposure();
            BluetoothServerSocket tmp = null;
            try {
                if (ActivityCompat.checkSelfPermission(Activity.getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions(Activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT},1234);
                        return;
                    }
                }
                Log.d(LOG_KEY, "Creating RFCOMM server socket...");
                tmp = adapter.listenUsingRfcommWithServiceRecord("Hal", UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                try {
                    Log.d(LOG_KEY, "Setting bluetooth hostname");
                    oldName = adapter.getName();
                    if(!adapter.setName("TOMASNET#" + new Random().nextInt())) throw new Exception("Could not set bluetooth hostname");
                } catch (Exception ex)
                {
                    Log.e(LOG_KEY, "Could not set bluetooth hostname");
                    ex.printStackTrace();
                }
            }
            catch (IOException e)
            {
                Log.e(LOG_KEY, "Socket's listen() method failed", e);
                e.printStackTrace();
            }
            mmServerSocket = tmp;
            listen();
        }

        private static void listen() {

            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    Log.d(LOG_KEY, "Waiting for client connection...");
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(LOG_KEY, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    Log.d(LOG_KEY, "A client was connected. Starting transfer...");
                    try {
                        OutputStream s = socket.getOutputStream();
                        s.write((Activity.CURRENT_DATA_PAYLOAD + "\r\n").getBytes());
                        s.flush();
                        s.close();
                        socket.close();
                        mmServerSocket.close();
                        Log.d(LOG_KEY, "Transfer finished. Closing socket...");
                        break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            Serve();
        }

        @SuppressLint("MissingPermission")
        public static void StopServer()
        {
            try {
                if(!oldName.equals(""))  adapter.setName(oldName);
            } catch (Exception ex)
            {
                Log.e(LOG_KEY, "Could not restore old bluetooth hostname");
                ex.printStackTrace();
            }
        }
    }
}
