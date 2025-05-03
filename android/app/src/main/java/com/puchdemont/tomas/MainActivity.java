package com.puchdemont.tomas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

    private boolean checkSystemWritePermission() {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(this);
            Log.d("TAG", "Can Write Settings: " + retVal);
            if(retVal){
                ///Permission granted by the user
            }else{
                //permission not granted navigate to permission screen
                openAndroidPermissionsMenu();
            }
        }
        return retVal;
    }

    private void openAndroidPermissionsMenu() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + this.getPackageName()));
        startActivity(intent);
    }


    private void connectToWifiAndGetData(ScanResult Network) {
        try {
            Log.d("WiFiScan", "Connecting to wifi network " + Network.SSID);

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

            String SSID = Network.SSID;
            String PassWD = "12345678";


            if (wifiManager != null) {
                WifiManager.WifiLock wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "TOMAS_WIFI_LOCK");
                wifiLock.acquire();

                // Android Q and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    if(!checkSystemWritePermission())
                    {
                        wifiManager.startScan();
                        return;
                    }

                    WifiNetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                            .setSsid(SSID)
                            .setWpa2Passphrase(PassWD)
                            .build();

                    NetworkRequest request = new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .setNetworkSpecifier(specifier)
                            .build();

                    ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                    ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(Network network) {
                            // Bind process to this network
                            cm.bindProcessToNetwork(network);
                            DownloadDataAndDisplay();
                        }
                    };

                    cm.requestNetwork(request, callback);
                }
                // Android 9 and below
                else
                {
                    // Configure the Wi-Fi network
                    WifiConfiguration wifiConfig = new WifiConfiguration();
                    wifiConfig.SSID = "\"" + SSID + "\""; // Enclose SSID in quotes
                    wifiConfig.preSharedKey = "\"" + PassWD + "\""; // Replace with the actual password

                    // Add the network and connect
                    int netId = wifiManager.addNetwork(wifiConfig);
                    if (netId != -1) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(netId, true);
                        wifiManager.reconnect();
                        Log.d("WiFiScan", "Successfully connected to " + Network.SSID);
                        DownloadDataAndDisplay();
                    } else {
                        Log.e("WiFiScan", "Failed to add network configuration for " + Network.SSID);
                    }
                }
                wifiLock.release();
            } else {
                Log.e("WiFiScan", "WifiManager is null, cannot connect to network");
            }
        } catch (Exception ex) {
            Log.e("WiFiScan", "Error connecting to WiFi network: " + ex.getMessage());
        }
    }

    // This method should be called after successfully connecting to a peer wifi network
    private void DownloadDataAndDisplay()
    {
        Log.e("WiFiScan", "Downloading data from the connected network");


        // download




    }

    private void CreateOwnHotSpot()
    {
        String SSID = NET_IDENTIFIER + "#" + CURRENT_VERSION_ID + "#" + new Random().nextInt();
        String PassWD = "12345678";


        // Android 7 cannot hotspot, xd
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiManager.LocalOnlyHotspotCallback callback = new WifiManager.LocalOnlyHotspotCallback() {
                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    super.onStarted(reservation);
                    Log.d("Hotspot", "Hotspot started with SSID: ");
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    Log.d("Hotspot", "Hotspot stopped");
                }

                @Override
                public void onFailed(int reason) {
                    super.onFailed(reason);
                    Log.e("Hotspot", "Failed to start hotspot. Reason: " + reason);
                }
            };

            wifiManager.startLocalOnlyHotspot(callback, null);
        } else {
            Log.e("Hotspot", "Hotspot creation is not supported on Android versions below Oreo.");
        }

    }

    private Airport getAirport(String json, ObjectMapper mapper) {
        try {
            return mapper.readValue(json, Airport.class);
        } catch (IOException e) {
            return null;
        }
    }
}
