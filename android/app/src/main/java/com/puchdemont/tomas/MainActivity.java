package com.puchdemont.tomas;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.widget.Toast;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    final private String NET_IDENTIFIER = "TOMASNET";
    private int CURRENT_VERSION_ID = -1;
    private ObjectMapper CURRENT_DATA = null;
    private String CURRENT_DATA_PAYLOAD = "{\"content\": \"HELLO WORLD!\"}";
    final UUID CONNECTION_UUID = UUID.fromString("969255c0-200a-11e0-ac64-c80d250c9a66");
    boolean continueDiscovery = false; // Flag to control discovery

    RecyclerView recyclerView;
    EjemploAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajustes de bordes del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        /*
        Flight flight1 = new Flight();
        flight1.setStatus("Delayed");
        Flight flight2 = new Flight();
        Flight flight3 = new Flight();
         */

        //List<Flight> ejemploVuelos = Arrays.asList(flight1, flight2, flight3);

        // Lista de ejemplo
        List<String> ejemplos = Arrays.asList(
                "Elemento 1", "Elemento 2", "Elemento 3",
                "Elemento 4", "Elemento 5"
        );

        adapter = new EjemploAdapter(ejemplos, item -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("text", item);
            startActivity(intent);
        });

        //Flight flight = new Flight();
        //flight.setStatus("Delayed");
        //adapter = new EjemploAdapter(ejemplos, item -> {
        //    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        //    intent.putExtra("flight", item);
        //    startActivity(intent);
        //});
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Register in onCreate or before discovery
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
            }, 1);
        }
        // registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        // wifiManager.startScan();
        BeginScanForNewServers();
        //ServeData();
    }

    private void BeginScanForNewServers()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        continueDiscovery = true;
        new Thread(() -> {
            while (continueDiscovery) {
                bluetoothAdapter.startDiscovery();
                try {
                    Thread.sleep(1000); // Wait for 10 seconds before restarting discovery
                } catch (InterruptedException e) {
                    Log.e("Bluetooth", "Discovery thread interrupted: " + e.getMessage());
                }
            }
        }).start();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress(); // Use this to connect
                // Optionally auto-connect or show in a list


                if (name != null && name.startsWith(NET_IDENTIFIER)) {
                    if (name.chars().filter(ch -> ch == '#').count() == 2)
                    {
                        int current_ver = Integer.parseInt(name.split("#")[1]);
                        if(current_ver > CURRENT_VERSION_ID) {
                            Log.e("WiFiScan", "BT Device "+ name +" IS VALID, CONNECTING!");
                            connectToBluetoothDevice(device, current_ver);
                        }
                        else {
                            Log.d("WiFiScan", "BT Device "+ name +" is not newer than the current data (" + current_ver + " <= " + CURRENT_VERSION_ID + ")");
                        }
                    }
                    else {
                        Log.d("WiFiScan", "BT Device "+ name +" does not match the NET_ID#{NUM}#DEV_UUID pattern ");
                    }
                }
                else {
                    if(name != null) Log.d("WiFiScan", "BT Device "+ name +" does not start with " + NET_IDENTIFIER);
                }
            }
        }
    };

    private void connectToBluetoothDevice(BluetoothDevice device, int new_version_id) {
        new Thread(() -> {
            try {
                BluetoothSocket socket = device.createRfcommSocketToServiceRecord(CONNECTION_UUID);
                socket.connect(); // Blocking
                continueDiscovery = false;

                OutputStream out = socket.getOutputStream();
                CURRENT_DATA_PAYLOAD = out.toString();
                CURRENT_VERSION_ID = new_version_id;
                Log.d("Bluetooth", "Got data:");
                Log.d("Bluetooth", CURRENT_DATA_PAYLOAD);
                socket.close();
                runOnUiThread(() -> ProcessData());
            } catch (IOException e) {
                Log.e("Bluetooth", "Error connecting to device: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public void ProcessData()
    {
        CURRENT_DATA = new ObjectMapper();
        getAirport(CURRENT_DATA_PAYLOAD, CURRENT_DATA);

        // load data to UI //

        ServeData();
    }

    private void ServeData() {

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30000000); // in seconds
        startActivity(discoverableIntent);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.setName(NET_IDENTIFIER + "#" + CURRENT_VERSION_ID + "#" + new Random().nextInt());

            new Thread(() -> {
                Log.d("BluetoothServer", "Starting server...");
                try {
                    BluetoothServerSocket serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("TOMAS_SERVER", CONNECTION_UUID);
                    Log.d("BluetoothServer", "Waiting for connection...");
                    BluetoothSocket socket = serverSocket.accept(); // Blocking call

                    Log.d("BluetoothServer", "Client connected: " + socket.getRemoteDevice().getName());
                    OutputStream out = socket.getOutputStream();
                    out.write(CURRENT_DATA_PAYLOAD.getBytes());
                    out.flush();
                    Log.d("BluetoothServer", "Data sent to client: " + CURRENT_DATA_PAYLOAD);

                    socket.close();
                    serverSocket.close();
                } catch (IOException e) {
                    Log.e("BluetoothServer", "Error in server: " + e.getMessage());
                }
            }).start();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        // unregisterReceiver(wifiScanReceiver);
    }

    private Airport getAirport(String json, ObjectMapper mapper) {
        try {
            return mapper.readValue(json, Airport.class);
        } catch (IOException e) {
            return null;
        }
    }
}
