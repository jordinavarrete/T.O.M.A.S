package com.puchdemont.tomas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //String text = getIntent().getStringExtra("text");
        //TextView textView = findViewById(R.id.textViewDetail);
        //textView.setText(text);

        //String v  = (String)getIntent().getSerializableExtra("flight");
        //Log.d("AAAAA", v);
        Flight flightdata = (Flight) getIntent().getSerializableExtra("flight");
        if (flightdata != null) {
            TextView statusText = findViewById(R.id.tvStatus);
            statusText.setText(flightdata.getStatus());
        } else {
            Log.e("DetailActivity", "Flight object is null!");
            Toast.makeText(this, "Error: no s'ha pogut carregar el vol", Toast.LENGTH_SHORT).show();
        }
    }
}