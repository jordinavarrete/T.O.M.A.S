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
        // Airport airport = getAirport("", CURRENT_DATA);
        ObjectMapper objectMapper = new ObjectMapper();
        Airport airport;
        try (InputStream is = getAssets().open("sample.json")) {
            // 2) Parseja directament a Airport
            airport = objectMapper.readValue(is, Airport.class);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error llegint sample.json", Toast.LENGTH_LONG).show();
            return;
        }
        List<String> vuelosInfo = airport.getFlights().stream()
                .map(f -> {
                    // Sacamos los códigos de vuelo (p. ej. IB1234, UX5678, …)
                    String codigos = f.getCode().stream()
                            .map(c -> c.getCompanyName() + c.getFlightNumber())
                            .collect(Collectors.joining(", "));
                    // Construimos la línea de texto
                    return String.format(
                            "Vuelo %s: %s → %s | Programado: %s | Real: %s | Estado: %s | T%s-G%s",
                            codigos,
                            f.getICAO(),           // aeropuerto origen/destino
                            f.getPresentCityName(),
                            f.getProgrammedArriveTimestamp(),
                            f.getActualArriveTimestamp() != null
                                    ? f.getActualArriveTimestamp()
                                    : "—",
                            f.getStatus(),
                            f.getLocation().getTerminal(),
                            f.getLocation().getGate()
                    );
                })
                .collect(Collectors.toList());
        vuelosInfo.forEach(System.out::println);
        */



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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        NearbyServer.Helper.Initialize( this);
        NearbyServer.Helper.StartAdvertising();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private Airport getAirport(String json, ObjectMapper mapper) {
        try {
            return mapper.readValue(json, Airport.class);
        } catch (IOException e) {
            return null;
        }
    }

    public void LoadDataFromString(String received) {
    }
}
