package com.puchdemont.tomas;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.content.IntentFilter;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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

        // Lista de ejemplo
        List<String> ejemplos = Arrays.asList(
                "Elemento 1", "Elemento 2", "Elemento 3",
                "Elemento 4", "Elemento 5"
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EjemploAdapter(ejemplos);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiManager.startScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiScanReceiver);
    }

    private final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                handleScanResults();
            }
        }
    };

    private void handleScanResults() {
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            if (wifiManager != null) {
                List<ScanResult> wifiList = wifiManager.getScanResults();
                for (ScanResult wifi : wifiList) {
                    Log.e("WiFiScan", "SSID: " + wifi.SSID + ", Signal Strength: " + wifi.level);
                }
                wifiManager.startScan();
            }
        } catch (Exception ex) {
            Log.e("WiFiScan", "Error handling WiFi scan results: " + ex.getMessage());
        }
    }
}
