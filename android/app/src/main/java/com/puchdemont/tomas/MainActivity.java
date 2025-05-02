package com.puchdemont.tomas;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String NET_IDENTIFIER = "TOMASNET";
    private int CURRENT_VERSION_ID = -1;
    private boolean CURRENT_DATA = false;

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
            ArrayList<ScanResult> candidates = new ArrayList<ScanResult>();
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            if (wifiManager != null) {
                List<ScanResult> wifiList = wifiManager.getScanResults();
                for (ScanResult wifi : wifiList) {
                    if (wifi.SSID.startsWith(NET_IDENTIFIER)) {
                        if (wifi.SSID.chars().filter(ch -> ch == '#').count() == 2)
                        {
                            int current_ver = Integer.parseInt(wifi.SSID.split("#")[1]);
                            if(current_ver > CURRENT_VERSION_ID) {
                                candidates.add(wifi);
                            }
                            else {
                                Log.d("WiFiScan", "Wifi network "+ wifi.SSID +" is not newer than the current data (" + current_ver + " <= " + CURRENT_VERSION_ID + ")");
                            }
                        }
                        else {
                            Log.d("WiFiScan", "Wifi network "+ wifi.SSID +" does not match the NET_ID#{NUM}#DEV_UUID pattern ");
                        }
                    }
                    else {
                        Log.d("WiFiScan", "Wifi network "+ wifi.SSID +" does not start with " + NET_IDENTIFIER);
                    }
                }

                if(candidates.isEmpty())
                {
                    Log.d("WiFiScan", "No compatible wifi versions found, will scan again");
                    new android.os.Handler().postDelayed(wifiManager::startScan, 5000);
                }
                else
                {
                    Log.d("WiFiScan", "Found " + candidates.size() + " candidates");
                    unregisterReceiver(wifiScanReceiver);
                    connectToWifiAndGetData(candidates.get(0));
                }
            }
        } catch (Exception ex) {
            Log.e("WiFiScan", "Error handling WiFi scan results: " + ex.getMessage());
        }
    }

    private void connectToWifiAndGetData(ScanResult Network)
    {
        Log.e("WiFiScan", "Connecting to wifi network " + Network.SSID);
    }
}
