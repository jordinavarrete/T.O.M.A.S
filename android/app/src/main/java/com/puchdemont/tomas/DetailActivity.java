package com.puchdemont.tomas;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DetailActivity extends AppCompatActivity {
    public static String getTimeHourMinute(String timestamp) {
        long epochSeconds = Long.parseLong(timestamp);
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        ZonedDateTime dateTime = instant.atZone(ZoneId.systemDefault()); // O especifica un fus horari concret
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Flight flightdata = (Flight) getIntent().getSerializableExtra("flight");
        if (flightdata != null) {
            TextView statusText = findViewById(R.id.tvFlightNumber);
            statusText.setText(flightdata.getCode().get(0).getFlightNumber());

            TextView statusText1 = findViewById(R.id.tvDestination);
            statusText1.setText(flightdata.getDestinationCity());

            TextView statusText2 = findViewById(R.id.tvStatus);
            statusText2.setText(flightdata.getStatus());

            TextView statusText3 = findViewById(R.id.tvDeptScheduled);
            statusText3.setText(getTimeHourMinute(flightdata.getProgrammedDepartTimestamp()));

            TextView statusText4 = findViewById(R.id.tvDeptEstimated);
            statusText4.setText(getTimeHourMinute(flightdata.getActualArriveTimestamp()));

            TextView statusText5 = findViewById(R.id.tvArrScheduled);
            statusText5.setText(getTimeHourMinute(flightdata.getProgrammedArriveTimestamp()));

            TextView statusText6 = findViewById(R.id.tvArrEstimated);
            statusText6.setText(getTimeHourMinute(flightdata.getActualArriveTimestamp()));

            TextView statusText7 = findViewById(R.id.tvDeptTerminal);
            statusText7.setText(flightdata.getLocation().getTerminal());

            TextView statusText8 = findViewById(R.id.tvDeptGate);
            statusText8.setText(flightdata.getLocation().getGate());

            TextView statusText9 = findViewById(R.id.tvArrDestinationAirport);
            statusText9.setText(flightdata.getIATA()+"/"+flightdata.getICAO());

            TextView statusText10 = findViewById(R.id.tvArrTerminal);
            statusText10.setText(flightdata.getDestinationTerminal());
        } else {
            Log.e("DetailActivity", "Flight object is null!");
            Toast.makeText(this, "Error: no s'ha pogut carregar el vol", Toast.LENGTH_SHORT).show();
        }
    }
}