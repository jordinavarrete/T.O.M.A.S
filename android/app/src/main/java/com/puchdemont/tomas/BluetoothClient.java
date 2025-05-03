package com.puchdemont.tomas;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

        static MainActivity Activity;
        static String Mac;
        private static BluetoothAdapter bluetoothAdapter;
        private static ArrayList<BluetoothDevice> scannedDevices = new ArrayList<>();
        private static ScanResult scanResult = null;

        @SuppressLint("MissingPermission")
        public static void Connect(MainActivity activity) {
            Activity = activity;
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            activity.registerReceiver(receiver, filter);

            scanNewestServer((device)->{
                if(device == null) return;
                Mac = device.getAddress();
                _connect();
            });
        }

        private static void _connect()
        {
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
        private static final BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String a = device.getName();
                    if(a!=null) {
                        Log.d("BT", a);
                        scannedDevices.add(device);
                    }
                }
            }
        };

        public static void scanNewestServer(ScanResult result) {
            scanResult = result;
            startScan();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                scanResult.onServerScanned(getNewestServer());
            }, 8000);
        }

        @SuppressLint("MissingPermission")
        public static void startScan() {
            scannedDevices.clear();
            bluetoothAdapter.startDiscovery();
        }

        @SuppressLint("MissingPermission")
        private static BluetoothDevice getNewestServer() {
            bluetoothAdapter.cancelDiscovery();

            for(BluetoothDevice d : scannedDevices) {
                String name = d.getName();
                if(name.startsWith("TOMASNET")) return d;
            }
            return null;
        }

        public static void onDestroy() {
            Activity.unregisterReceiver(receiver);
        }
    }
    public interface ScanResult {
        void onServerScanned(BluetoothDevice device);
    }
}
