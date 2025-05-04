package com.puchdemont.tomas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.widget.ProgressBar;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    final private String NET_IDENTIFIER = "TOMASNET";
    private int CURRENT_VERSION_ID = -1;
    private Airport CURRENT_DATA = new Airport();
    private final ObjectMapper Mapper = new ObjectMapper();
    public String CURRENT_DATA_PAYLOAD = "{\"content\": \"HELLO WORLD!\"}";
    final UUID CONNECTION_UUID = UUID.fromString("969255c0-200a-11e0-ac64-c80d250c9a66");
    boolean continueDiscovery = false; // Flag to control discovery

    RecyclerView recyclerView;
    EjemploAdapter adapter;
    Button forceAdmin;
    private ProgressBar loadingPB;
    private boolean isProgressVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES, Manifest.permission.BLUETOOTH_SCAN}, 3343);
        } else {
            BluetoothClient.Helper.Connect(this);
        }
        //ProgressBar
        loadingPB = findViewById(R.id.progressBar);

        // Ajustes de bordes del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        forceAdmin = findViewById(R.id.forceServerButton);

        forceAdmin.setOnClickListener(view -> loadSamplePayload());

        adapter = new EjemploAdapter(new ArrayList<Flight>(), item -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("flight", item);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 3343) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ConnectToServer();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 1234) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                BluetoothServer.Helper.Serve();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    boolean hasDoneStart = false;


    @Override
    protected void onStart() {
        super.onStart();

        if(hasDoneStart) return;
        hasDoneStart = true;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN}, 3343);
        } else {
            ConnectToServer();
        }
    }

    boolean _connected = false;
    public void ConnectToServer()
    {
        // if(_connected) return;
        _connected = true;
        BluetoothClient.Helper.Connect( this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BluetoothServer.Helper.StopServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothClient.Helper.onDestroy();
    }

    public void LoadDataFromString(String received) {
        if(received.isEmpty())
        {
            Toast.makeText(this, "Empty payload, retrying", Toast.LENGTH_LONG).show();
            BluetoothClient.Helper.Connect(this);
            return;
        }

        try {
            CURRENT_DATA_PAYLOAD = received;
            CURRENT_DATA = Mapper.readValue(received, Airport.class);

            List<Flight> flights = CURRENT_DATA.getFlights();
            adapter = new EjemploAdapter(flights, item -> {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("flight", item);
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            // Start Server
            BluetoothClient.Helper.onDestroy();
            BluetoothServer.Helper.InitializeAndServe(this);
        } catch (Exception ex)
        {
            Log.e("MainActivity", "Error parsing JSON", ex);
        }
    }

    private void loadSamplePayload() {
        LoadDataFromString(SampleJson.json);
    }

    public void toggle_progressBar() {
        if (isProgressVisible) {
            // Hide progress bar
            loadingPB.setVisibility(View.GONE);
            isProgressVisible = false;
        } else {
            // Show progress bar
            loadingPB.setVisibility(View.VISIBLE);
            isProgressVisible = true;
        }
    }
}
