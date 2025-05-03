package com.puchdemont.tomas;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;


public class BluetoothClient {
    public static class Helper {

        static MainActivity Activity;
        static String Mac;

        @SuppressLint("MissingPermission")
        public static void Connect(String MAC, MainActivity activity) {
            Activity = activity;
            Mac = MAC;
        }

        private static void _connect()
        {

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                // Bluetooth is not available or not enabled
                return;
            }

            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(Mac);
            BluetoothSocket socket = null;

            try {
                // Use a well-known UUID for SPP (Serial Port Profile)
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                socket = device.createRfcommSocketToServiceRecord(uuid);
                socket.connect();
                ReadFromSocket(socket);

                // Connection successful
            } catch (IOException e) {
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
                try {
                    while ((line = reader.readLine()) != null) {
                        // Process the received data
                        System.out.println("Received: " + line);
                        result += line + "\n";
                    }
                } catch (IOException e) {
                    Activity.LoadDataFromString(result);
                    return;
                }
                _connect();
            } catch (Exception ex)
            {
                ex.printStackTrace();
                _connect();
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
    }
}
