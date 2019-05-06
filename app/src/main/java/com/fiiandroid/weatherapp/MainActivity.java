package com.fiiandroid.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity {

    private static final int INTERNET_LOCATION_PERMISSIONS = 1;
    private FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        JSONObject weatherResult = getData();
        TextView temperatureTextView = findViewById(R.id.temperatureLabel);
        String currentText = temperatureTextView.getText().toString();
        Objects.requireNonNull(weatherResult);
        try {
            String updatedText = currentText + " " + weatherResult.getJSONObject("main").getDouble("temp") + "\u00B0C";
            temperatureTextView.setText(updatedText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermissions(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case INTERNET_LOCATION_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getData();
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).
                            setMessage("We cannot provide the weather information without internet access!")
                            .setTitle("Error Weather")
                            .create();
                    alertDialog.show();
                }

        }
    }

    @SuppressLint("MissingPermission")
    private JSONObject getData() {
        String[] permissions = new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!checkPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, INTERNET_LOCATION_PERMISSIONS);
        }
        LocationTask locationTask = new LocationTask();
        try {
            return locationTask.execute(locationProviderClient).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
