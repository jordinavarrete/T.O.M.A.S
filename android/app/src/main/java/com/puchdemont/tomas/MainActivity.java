package com.puchdemont.tomas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    final private String NET_IDENTIFIER = "TOMASNET";
    private int CURRENT_VERSION_ID = -1;
    private ObjectMapper CURRENT_DATA = null;
    public String CURRENT_DATA_PAYLOAD = "{\"content\": \"HELLO WORLD!\"}";
    final UUID CONNECTION_UUID = UUID.fromString("969255c0-200a-11e0-ac64-c80d250c9a66");
    boolean continueDiscovery = false; // Flag to control discovery

    RecyclerView recyclerView;
    EjemploAdapter adapter;

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
            BluetoothServer.Helper.InitializeAndServe(this);
        }

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

    @Override
    protected void onStart() {
        super.onStart();

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
        //_connected = true;
        //BluetoothClient.Helper.Connect("A0:7D:9C:DE:FA:AF", this);
        BluetoothServer.Helper.InitializeAndServe(this);
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

    private Airport getAirport(String json, ObjectMapper mapper) {
        try {
            return mapper.readValue(json, Airport.class);
        } catch (IOException e) {
            return null;
        }
    }

    public void LoadDataFromString(String received) {
        boolean dataValid = false;
        _connected = false;

        // Start Server

    }
}
