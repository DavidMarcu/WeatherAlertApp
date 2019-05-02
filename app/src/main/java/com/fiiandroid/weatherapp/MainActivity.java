package com.fiiandroid.weatherapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity {

    private static final int INTERNET_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestInternetService();
    }

    private void requestInternetService() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION);
        } else {
            getData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case INTERNET_PERMISSION:
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


    private void getData() {
        WeatherApiCall weatherApiCall = new WeatherApiCall();
        weatherApiCall.execute("weather", "Ploiesti,ro", "metric");
        try {
            JSONObject weatherApiResult = new JSONObject(weatherApiCall.get());
            TextView temperature = findViewById(R.id.temperatureLabel);
            CharSequence labelText = temperature.getText();
            String temperatureText = labelText + " " + weatherApiResult.getJSONObject("main").getDouble("temp");
            temperature.setText(temperatureText);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
